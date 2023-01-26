package hm.site.projectName.board.notice.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.ModelMap;

import core.exception.HMException;
import core.util.PropertiesUtil;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import hm.site.projectName.board.notice.mapper.NoticeMapper;
import hm.site.projectName.board.notice.service.NoticeService;
import hm.site.projectName.common.file.service.CommFileService;
import hm.site.vo.extend.RaBbsTbExVO;
import hm.site.vo.extend.RaUserTbExVO;


@Service
public class NoticeServiceImpl implements NoticeService {

	@Resource
	private NoticeMapper noticeMapper;

	@Resource
	private CommFileService commFileService;

	@Autowired
	private HttpSession session;


	@Override
	public void selectNoticeList(ModelMap model, RaBbsTbExVO raBbsTbExVO) throws Exception {

		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(raBbsTbExVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(raBbsTbExVO.getPageUnit());
		paginationInfo.setPageSize(raBbsTbExVO.getPageSize());

		raBbsTbExVO.setFirstIndex(paginationInfo.getFirstRecordIndex() + 1);
		raBbsTbExVO.setLastIndex(paginationInfo.getLastRecordIndex());
		raBbsTbExVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		int listCnt = noticeMapper.selectNoticeListCnt(raBbsTbExVO);

		paginationInfo.setTotalRecordCount(listCnt);
		model.addAttribute("paginationInfo", paginationInfo);

		if (listCnt > 0) {
			List<RaBbsTbExVO> list = noticeMapper.selectNoticeList(raBbsTbExVO);
			model.addAttribute("list", list);
		}
		model.addAttribute("listCnt", listCnt);

	}

	@Override
	public RaBbsTbExVO selectNotice(RaBbsTbExVO raBbsTbExVO) throws Exception {

		RaBbsTbExVO data = noticeMapper.selectNotice(raBbsTbExVO);

		return data;

	}

	@Override
	@Transactional
	public void insertNotice(RaBbsTbExVO raBbsTbExVO) throws Exception {

		/***********************************************************************
		 * 1. 첨부파일 처리
		 ***********************************************************************/
		// 파일 처리
		Map<String, Object> resultFile = commFileService.crudFiles(PropertiesUtil.getProperty("globals.sample.allowFileType"), "notice", raBbsTbExVO.getAtchFileNo(), raBbsTbExVO.getAtchFileDelSn(), Integer.parseInt(PropertiesUtil.getProperty("globals.notice.maxFileCnt")), raBbsTbExVO.getAtchFiles());

		// 파일 처리 실패일 경우
		if  ( !(boolean) resultFile.get("bool") )  {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

			throw new HMException("파일 처리에 실패하였습니다.", null, false);
		}

		/***********************************************************************
		 * 2. 등록 처리
		 ***********************************************************************/
		RaUserTbExVO loginInfo = (RaUserTbExVO) session.getAttribute("loginInfo");
		raBbsTbExVO.setAtchFileNo((Integer) resultFile.get("fileNo"));
		raBbsTbExVO.setRegistUserId(loginInfo.getLoginId());
		raBbsTbExVO.setUpdateUserId(loginInfo.getLoginId());

		noticeMapper.insertNotice(raBbsTbExVO);

	}

	@Override
	@Transactional
	public void updateNotice(RaBbsTbExVO raBbsTbExVO) throws Exception {

		/***********************************************************************
		 * 1. 첨부파일 처리
		 ***********************************************************************/
		// 파일 처리
		Map<String, Object> resultFile = commFileService.crudFiles(PropertiesUtil.getProperty("globals.sample.allowFileType"), "notice", raBbsTbExVO.getAtchFileNo(), raBbsTbExVO.getAtchFileDelSn(), Integer.parseInt(PropertiesUtil.getProperty("globals.notice.maxFileCnt")), raBbsTbExVO.getAtchFiles());

		// 파일 처리 실패일 경우
		if  ( !(boolean) resultFile.get("bool") )  {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

			throw new HMException("파일 처리에 실패하였습니다.", null, false);
		}

		/***********************************************************************
		 * 2. 수정 처리
		 ***********************************************************************/
		RaUserTbExVO loginInfo = (RaUserTbExVO) session.getAttribute("loginInfo");
		raBbsTbExVO.setAtchFileNo((Integer) resultFile.get("fileNo"));
		raBbsTbExVO.setUpdateUserId(loginInfo.getLoginId());

		noticeMapper.updateNotice(raBbsTbExVO);

	}

	@Override
	@Transactional
	public void deleteNotice(RaBbsTbExVO raBbsTbExVO) throws Exception {

		/***********************************************************************
		 * 1. 삭제 처리
		 ***********************************************************************/
		noticeMapper.deleteNotice(raBbsTbExVO);

	}
	@Override
	public List<RaBbsTbExVO> selectNoticeScheduleList(ModelMap model, String month) throws Exception {
		
		RaBbsTbExVO raBbsTbExVO = new RaBbsTbExVO();
		raBbsTbExVO.setRegistDate(month);
 		List<RaBbsTbExVO> list = noticeMapper.selectNoticeList(raBbsTbExVO);
 		return list;
	}

}
