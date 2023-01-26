package hm.site.projectName.board.notice.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import core.util.PropertiesUtil;
import core.util.StringUtil;
import hm.site.projectName.board.notice.service.NoticeService;
import hm.site.projectName.common.code.service.CommCodeService;
import hm.site.projectName.common.file.service.CommFileService;
import hm.site.vo.extend.RaBbsTbExVO;



/**
 * @Class   	: BoardController.java
 * @Description : 게시물관리 관련 Controller
 * @생성일자  : 2021. 9. 2
 */
@Controller
@RequestMapping(value = "/board/notice")
public class NoticeController {

	@Resource
	private NoticeService noticeService;

	@Resource
	private CommCodeService commCodeService;

	@Resource
	private CommFileService commFileService;


	/**
	 * 공지사항 목록 페이지
	 * @param model
	 * @param raBbsTbExVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/noticeList.do")
	public String list( ModelMap model, @ModelAttribute RaBbsTbExVO raBbsTbExVO, @RequestParam(value = "goViewNttNo", required = false) Integer goViewNttNo) throws Exception {

		if  ( StringUtil.isNotEmpty(goViewNttNo) )  {
			//상세가기 번호가 존재하면 상세페이지 이동
			return "redirect:/board/notice/noticeView.do?nttNo="+goViewNttNo;
		}

		// 공지사항 목록
		noticeService.selectNoticeList(model, raBbsTbExVO);

		return "board/notice/noticeList.cn";

	}
	/**
	 * 공지사항 목록 페이지
	 * @param model
	 * @param raBbsTbExVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/noticeScheduleList.do")
	public String schedule( ModelMap model
							, @RequestParam(value = "month", required = false) String month) throws Exception {
		
		if(StringUtil.isEmpty(month)) {
			// 현재 날짜 구하기
	        LocalDate now = LocalDate.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	        // 포맷 적용
	        String formatedNow = now.format(formatter);
	        month = StringUtil.getDateYYYYMM(formatedNow, "");
	        System.out.println(month);
	     // 공지사항 목록
		}
		List<RaBbsTbExVO> list = noticeService.selectNoticeScheduleList(model, month);
 		model.addAttribute("list", list);
		return "board/notice/noticeScheduleList.cn";
	
	       
	}
	/**
	 * 공지사항 저장 처리
	 * @param raBbsTbExVO
	 * @param atchFiles
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/noticeScheduleListSelect.do", method = RequestMethod.POST)
	@ResponseBody
	public List<RaBbsTbExVO> scheduleSelect( ModelMap model
			, @RequestParam(value = "month", required = false) String month) throws Exception {


		List<RaBbsTbExVO> list = noticeService.selectNoticeScheduleList(model, month);
		return list;

	}

	/**
	 * 공지사항 등록/수정 페이지
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/noticeSave.do")
	public String save(
			ModelMap model
			, @ModelAttribute RaBbsTbExVO raBbsTbExVO) throws Exception {

		// 업무 유형 코드
		model.addAttribute("jobTyCodeList", commCodeService.selectSubCodeList("COM004"));
		// 공지사항 상세
		if  ( StringUtil.isNotEmpty(raBbsTbExVO.getNttNo()) )  {
			RaBbsTbExVO data =noticeService.selectNotice(raBbsTbExVO);
			model.addAttribute("data", noticeService.selectNotice(raBbsTbExVO));
			if (data != null && data.getAtchFileNo() != null) {
				model.addAttribute("atchFile", commFileService.selectFileList(data.getAtchFileNo()));
			}
		}
		String allowFileType = PropertiesUtil.getProperty("globals.sample.allowFileType");
		model.addAttribute("allowFileType", allowFileType);

		return "board/notice/noticeSave.cn";

	}

	/**
	 * 공지사항 저장 처리
	 * @param raBbsTbExVO
	 * @param atchFiles
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/noticeSaveProc.do", method = RequestMethod.POST)
	public String saveProc(
			RaBbsTbExVO raBbsTbExVO
			, @RequestParam(value = "atchFile") List<MultipartFile> atchFiles) throws Exception {


 		raBbsTbExVO.setAtchFiles(atchFiles);


		if  ( raBbsTbExVO.getNttNo() == null || raBbsTbExVO.getNttNo() == 0)  {
			// 등록
			noticeService.insertNotice(raBbsTbExVO);
		}
		else  {
			// 수정
			noticeService.updateNotice(raBbsTbExVO);
		}

		return "redirect:/board/notice/noticeList.do";

	}

	/**
	 * 공지사항 상세 페이지
	 * @param model
	 * @param raBbsTbExVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/noticeView.do")
	public String view(ModelMap model
			, @RequestParam(value = "nttNo", required = false) int nttNo, @ModelAttribute RaBbsTbExVO raBbsTbExVO) throws Exception {
		if(StringUtil.isNotEmpty(nttNo)) {
			raBbsTbExVO.setNttNo(nttNo);
		}
		// 업무 유형 코드
		model.addAttribute("jobTyCodeList", commCodeService.selectSubCodeList("COM004"));
		// 공지사항 상세
		model.addAttribute("data", noticeService.selectNotice(raBbsTbExVO));

		return "board/notice/noticeView.cn";

	}

	/**
	 * 공지사항 삭제 처리
	 * @param raBbsTbExVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/noticeDeleteProc.do", method = RequestMethod.POST)
	public String deleteProc(
			RaBbsTbExVO raBbsTbExVO) throws Exception {

		// 삭제
		noticeService.deleteNotice(raBbsTbExVO);

		return "redirect:/board/notice/noticeList.do";

	}

}