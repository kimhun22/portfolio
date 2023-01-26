package hm.site.projectName.board.notice.service;

import java.util.List;

import org.springframework.ui.ModelMap;

import hm.site.vo.extend.RaBbsTbExVO;





public interface NoticeService {

	/**
	 * 공지사항 목록 조회
	 * @param model
	 * @param raBbsTbExVO
	 * @throws Exception
	 */
	void selectNoticeList(ModelMap model, RaBbsTbExVO raBbsTbExVO) throws Exception;
	/**
	 * 공지사항 스케줄 조회
	 * @param model
	 * @param raBbsTbExVO
	 * @throws Exception
	 */
	List<RaBbsTbExVO> selectNoticeScheduleList(ModelMap model, String month) throws Exception;

	/**
	 * 공지사항 단일 조회
	 * @param raBbsTbExVO
	 * @return
	 * @throws Exception
	 */
	RaBbsTbExVO selectNotice(RaBbsTbExVO raBbsTbExVO) throws Exception;

	/**
	 * 공지사항 등록
	 * @param raBbsTbExVO
	 * @return
	 * @throws Exception
	 */
	void insertNotice(RaBbsTbExVO raBbsTbExVO) throws Exception;

	/**
	 * 공지사항 수정
	 * @param raBbsTbExVO
	 * @return
	 * @throws Exception
	 */
	void updateNotice(RaBbsTbExVO raBbsTbExVO) throws Exception;

	/**
	 * 공지사항 삭제
	 * @param raBbsTbExVO
	 * @throws Exception
	 */
	void deleteNotice(RaBbsTbExVO raBbsTbExVO) throws Exception;

}
