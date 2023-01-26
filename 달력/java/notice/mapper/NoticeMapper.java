package hm.site.projectName.board.notice.mapper;

import java.util.List;

import hm.site.vo.extend.RaBbsTbExVO;
import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper
public interface NoticeMapper {

	/**
	 * 공지사항 목록 갯수 조회
	 * @param raBbsTbExVO
	 * @return
	 */
	int selectNoticeListCnt(RaBbsTbExVO raBbsTbExVO) throws Exception;

	/**
	 * 공지사항 목록 조회
	 * @param raBbsTbExVO
	 * @return
	 * @throws Exception
	 */
	List<RaBbsTbExVO> selectNoticeList(RaBbsTbExVO raBbsTbExVO) throws Exception;

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
	int insertNotice(RaBbsTbExVO raBbsTbExVO) throws Exception;

	/**
	 * 공지사항 수정
	 * @param raBbsTbExVO
	 * @return
	 * @throws Exception
	 */
	int updateNotice(RaBbsTbExVO raBbsTbExVO) throws Exception;

	/**
	 * 공지사항 삭제
	 * @param raBbsTbExVO
	 * @return
	 * @throws Exception
	 */
	int deleteNotice(RaBbsTbExVO raBbsTbExVO) throws Exception;

}
