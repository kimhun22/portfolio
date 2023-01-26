package kr.co.polymer.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.heartmedia.core.data.Paging;

import kr.co.polymer.mapper.site.QuestionAttachmentMapper;
import kr.co.polymer.mapper.site.QuestionMapper;
import kr.co.polymer.view.StringUtil;
import kr.co.polymer.vo.Question;
import kr.co.polymer.vo.QuestionAttachment;
import kr.co.polymer.vo.excel.InquiryExcelVO;

@Service("questionService")
public class QuestionService
{


	@Resource(name = "questionMapper")
	QuestionMapper questionMapper;

	@Resource(name = "questionAttachmentMapper")
	QuestionAttachmentMapper questionAttachmentMapper;

	@Resource(name = "emailService")
	EmailService emailService;

	@Value("#{appConfig['email.addr']}")
	String emailAddr;

	@Value("#{appConfig['upload.rootPath']}")
	String rootPath;
	/*
	 * 관련문의 리스트 갯수 가져오기
	 */
	public int count(Map<String, Object> where) {
		return questionMapper.count(where);
	}

	/*
	 * 관련문의 리스트 가져오기
	 */
	public List<Question> list(Map<String, Object> where, Paging paging) {
		return questionMapper.list(where, paging);
	}

	/*
	 * 관련문의 상세보기
	 */
	public Question get(int sequence) {
		return questionMapper.get(sequence);
	}

	/*
	 * 관련문의 등록
	 */
	public void insert(Question question) {
		questionMapper.insert(question);

		// 첨부파일 정보 등록
		if (question.getAttachList() != null)
		{
			for (QuestionAttachment attach : question.getAttachList())
			{
				attach.setQuestion(question.getSequence());
				questionAttachmentMapper.insert(attach);
			}
		}

	}

	/*
	 * 회원 리스트 엑셀다운로드
	 */
	public List<InquiryExcelVO> excel(Map<String, Object> where) {
		return questionMapper.excel(where);
	}

	/*
	 * 이메일 발송
	 */
	public void emailSend(Question question) {

		List<QuestionAttachment> questionAttachList = questionAttachmentMapper.list(question.getSequence());

		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, String> email = new HashMap<String, String>();

		dataMap.put("data", question);

		email = emailKeywordSet(question.getContent());

		String to = email.get("to");
		String cc = email.get("cc");



		if (questionAttachList != null && questionAttachList.size() > 0)
		{
			List<Map<String, Object>> attachList = new ArrayList<Map<String, Object>>();

			for (QuestionAttachment questionAttach : questionAttachList)
			{
				File attachFile = new File(rootPath + File.separator + questionAttach.getUuid());

				Map<String, Object> attachMap = new HashMap<String, Object>();
				attachMap.put("fileName", questionAttach.getFileName());
				attachMap.put("attach", attachFile);

				attachList.add(attachMap);
			}


			  emailService.sendMail(question.getTitle(), question.getEmail(), "고분자 시스템",
			  to, cc, dataMap, "vm/inquiry.vm", attachList);

		}
		else
		{

			  emailService.sendMail(question.getTitle(), question.getEmail(), "고분자 시스템",
			  to, cc, dataMap, "vm/inquiry.vm");

		}
	}

	/*
	 * 관련문의 수정
	 */
	public void update(Question question) {
		questionMapper.update(question);

		// 첨부파일 정보 수정
		questionAttachmentMapper.delete(question.getSequence());
		if (question.getAttachList() != null)
		{
			// 첨부파일 정보 DB저장
			for (QuestionAttachment attach : question.getAttachList())
			{
				attach.setQuestion(question.getSequence());
				questionAttachmentMapper.insert(attach);
			}
		}
	}

	/*
	 * 관련문의 삭제
	 */
	public void delete(int sequence, String status) {
		questionMapper.delete(sequence, status);
	}

	/*
	 * 첨부파일 정보 가져오기
	 */
	public QuestionAttachment getAttach(int sequence) {
		return questionAttachmentMapper.get(sequence);
	}

	public Map<String, String> emailKeywordSet(String body) {


		String[][] emailKeyword ={
				//행정
				{ "hexafluoroisopropanol", "HFIP", "TFE", "TOSOH", "trifluoroethanoltrifluoroethanol",
					"TSKgel", "결제", "불소", "사업자등록번호", "사업자번호", "위,변조", "위변조", "토소" },
				//2page
				{ "고분자 점도", "고분자점도", "고유 점도", "고유점도", "극한 점도", "극한점도", "동점도", "모세관", "상대 점도", "상대점도", "오일 점도", "오일점도",
					"중합도", "absolute viscosity", "Canon-Fenske", "degree of polymerization", "electrical paper",
					"glass capillary viscometer", "inherent viscosity", "intrinsic viscosity",
					"kinematic viscosity", "reduced viscosity", "relative viscosity", "Scavenging Activity",
					"Ubbelohde", "viscosity number", "viscosity of polymer solution", "ASTM D445", "ASTM D4603",
					"ISO 1628-1", "ISO 307", "KS C IEC 60450", "등록용", "면제", "안정성", "화평법", "stability test", "물질등록",
					"등록" },
				//3page
				{ "NCO가", "강열감량", "건조감량", "고정탄소", "고형분 ", "고형연료제품", "공업 분석", "공업분석", "과산화물가", "과산화수소", "내산성", "내약품",
						"내화학", "냄새", "미네랄", "발열량", "비누화", "산가", "세정검증", "수산기가", "수소이온농도", "수은", "수질", "수질오염공정", "아민가",
						"양이온", "에폭시가", "요오드가", "용출", "원자 흡수 분광법", "의약품", "이소시아네이트가", "이온결합플라즈마", "이온교환", "이온크로마토그래피",
						"적정 분석", "적정분석", "전원소", "중금속", "할로겐", "회분", "휘발분", "음이온", "이온 교환", "이온 크로마토그래피", "acid value",
						"amine value", "Ash", "Atomic absorption spectroscopy", "Bromide", "C, H, N, S, O",
						"calorimeter", "chemical resistance", "Chloride", "C-IC", "CS", "dissolved oxygen", "DO", "EA",
						"electric conductivity", "Energy dispersive X-ray flourescence spectrometry", "epoxy value",
						"Fixed carbon", "Fluoride", "heavy metal", "hydrogen peroxide", "hydroxy value", "IC", "ICP",
						"ICP-MS", "ignition loss", "Inductively Coupled Plasma Optical Emission Spectrometer", "Iodide",
						"Ion Chromatography", "ion exchange capacity", "Isocyanate value", "Mercury", "Mineral",
						"Nitrate", "Nitrite", "OBC", "OES", "OH value", "ONH", "optical emission", "ORP",
						"oxidation-reduction potential", "oxygen bomb combustion", "peroxide value", "pH", "Phosphate",
						"proximate analysis", "saponification value", "spark emission", "Sulfate", "TDS",
						"tetramethylammonium hydroxide", "titration", "TMAH", "total dissolved solids", "Validation",
						"Volatile", "Wavelength Dispersive X-ray flourescence spectrometry", "WD-XRF ", "XRD", "XRF",
						"ASTM D1500", "ASTM D2270", "ASTM D445", "ASTM D4739", "ASTM D6377", "ASTM D664", "ASTM D6980",
						"ASTM D746", "ASTM D7582", "ASTM D893", "ASTM D93", "ASTM D97", "EP시험", "ICH Q3D", "IEC 62321",
						"ISO 3451", "ISO 4406", "ISO 974", "KP시험", "KS D 1652", "KS H ISO3960 ", "KS I 3206",
						"KS I ISO17294", "KS M 0032", "KS M 0034", "KS M 0065", "KS M 5000", "KS M 8013", "UP시험",
						"원소분석", "수은 분석", "탄소", "황", "이온", "금속", "유기키트", "밸리데이션", "금속불순물", "의약품 원료", "금속재질", "에폭시 당량",
						"대한약전", "약전", "ICP-OES", "ED-XRF", "S-OES", "RoHS", "impurity", "C-IC", "USP", "EP", "KP",
						"ICH", "KS H ISO 3961", "KS M 3828", "KS M ISO 3001", "KS M 3829", "KS M 1207", "KS M ISO 6353",
						"암모늄", "금속성분", "KS M 1403"
				},
				//5page
				{
						"간이생분해", "검출기", "끓는점", "물리화학", "밀폐식", "발화점", "분배 계수", "분배계수", "분자량", "비점", "빙점", "세타", "수용성",
						"수용해도", "어는점", "인화성", "인화점", "잠열", "증기압", "증류법", "총유기탄소", "히알루론산", "appearance",
						"auto-ignition temp", "boiling point", "bp", "Closed Cup", "DMF", "ELSD", "flammability",
						"flash point", "freezing point", "GPC", "inorganic carbon", "Latent heat", "Maldi-tof", "NMP",
						"partition coefficient", "seta flash", "THF", "TOC", "total carbon", "total organic carbon",
						"vapor pressure", "water solubility", "ASTM D1929", "ASTM D3828", "ASTM D5296", "EC Method A10",
						"KS I ISO 8245", "KS ISO 15814", "KS ISO 7579", "OECD 102", "OECD 103", "OECD 104", "OECD 105",
						"OECD 107", "OECD 117", "OECD 118", "OECD 120", "OECD 122",
						// 추가
						"cleaning validation", "세척증명", "수분함량", "칼피셔", "함수율", "Karl fischer", "Moisture",
						"moisture content", "ASTM D6304", "ASTM D6869", "ISO 15512", "ISO 760", "KS M 0010", "수분",
						"MSDS" },
				// 6page
				{ "180도", "90도", "가공", "가소성", "경도", "고저항", "고주파", "굴곡", "나노인덴터", "낙추", "난연", "내아크성", "내장재", "내전압",
						"내충격", "노치", "동박적층판", "두드림", "듀로미터", "뚫림", "로드셀", "로크웰", "루프택", "룹택", "마찰계수", "만능시험기", "모듈러스",
						"몰드수축", "물성", "바콜", "박리", "발포", "방열", "밴딩", "벤딩", "변형", "변형률", "변형율", "보강간섭", "부도체", "분체",
						"비규격", "비열", "비중", "비캇", "비커스", "비틀림", "사이클", "사출", "샤르피", "성형수축", "소성 영역", "소성영역", "스크래치",
						"시편", "신율", "아령형", "아이조드", "압자", "압축", "압축 강도", "압축강도", "연소성", "연신률", "연신율", "열변형", "열선법",
						"열전도", "열확산", "유전률", "유전율", "유전체", "유지력", "응력", "이온전도", "인열", "인장", "인장 하중", "인장하중", "자동차",
						"저주파", "저항", "전기전도", "전단", "전자파", "절연", "절연지", "점착", "접착력", "접착테이프", "찢김", "챔버", "충격", "캔틸레버",
						"크리프", "크립", "탄성", "탄성 영역", "탄성계수", "탄성률", "탄성영역", "탄성율", "탐침", "태핑", "탭핑", "트래킹", "트랙킹",
						"파단강도", "펀칭", "평판열류", "포아송", "푸아송", "피로", "피에조", "피혁", "하중", "항복", "부착력", "마찰력", "Dielectric",
						"Puncture", "용융", "점착", "연필", "ASTM C518", "ASTM D1002", "ASTM D1004", "ASTM D1238",
						"ASTM D149", "ASTM D150", "ASTM D1525", "ASTM D1708", "ASTM D1876", "ASTM D1894", "ASTM D2240",
						"ASTM D2295", "ASTM D2303", "ASTM D256", "ASTM D257", "ASTM D3163", "ASTM D412", "ASTM D4830",
						"ASTM D495", "ASTM D5868", "ASTM D638", "ASTM D648", "ASTM D695", "ASTM D785", "ASTM D790",
						"ASTM D882", "ASTM E1461", "D903", "FMVSS 302", "IEC 60112", "IEC 60243", "ISO 180", "ISO 527",
						"ISO1133", "JIS C 2138", "JIS C 6481", "JIS K 7113", "JIS K7171", "KS C2105", "KS K 60335",
						"KS M 3015", "KS M ISO 178", "KS M ISO 179", "KS M ISO 180", "KS M ISO 306", "KS M ISO 527",
						"KS M ISO 60243", "KS M ISO 604", "KS T 1028", "MS 300-08", "MS200", "MS210", "MS211", "MS213",
						"MS215", "MS216", "MS221", "MS225", "MS235", "MS2500", "MS256", "MS300", "MS311", "MS373",
						"MS382", "MS655", "MS700", "MS711", "MS725", "MS731", "MS941", "UL746B", "ASTM D1706",
						"ASTM D2583", "ASTM D3330", "ASTM D3363", "ASTM D3518", "ASTM D3763", "ASTM D3846",
						"ASTM D6195", "ASTM D624", "ASTM D732", "ASTM D991", "ASTM E92", "IPC TM 650 2.5.1",
						"IPC TM 650 2.5.5.9", "IEC 60695", "KS M 6518", "ASTM D3359", "ASTM D395", "ASTM D746",
						"ISO 812", "KS M6518", "KS M ISO 4662", "ISO 974" },
				// 7page (삭제 "PAHs")
				{ "가소제", "계면활성제", "방부제", "보존제", "살균보존제", "순도", "실록산", "아세트알데하이드", "아웃가스", "잔류 단량체", "잔류 용매", "잔류단량체",
						"잔류용매", "절연유", "정량 분석", "정량분석", "정성 분석", "정성분석", "천연물", "크로마토", "크로마토그래피", "특정 성분", "특정성분",
						"포르말린", "포름알데히드", "퓨란", "헤드스페이스", "혼유", "휘발", "2-acetylfuran", "2-furaldehyde",
						"2-furfuryl alcohol, 5-Methylfurfural", "5-hydroxymethyl-2-furaldehyde", "acetaldehyde", "Area",
						"BHT", "bisphenol", "cal curve", "calibration curve", "chromatograph", "cyclosiloxane", "D3",
						"D4", "D5", "D6", "D7", "D8", "diode array detector", "Diode Array Detector",
						"Evaporative Light Scattering Detector", "formaldehyde", "furan", "GC", "GC/MS", "GC-FID",
						"GCMS", "HEMA", "High Performance Liquid Chromatography", "HPLC", "HS-", "identification", "LC",
						"LC/MS", "library", "MassSpectrometer", "NDMA", "out gas", "out-gas", "Poloxamer",
						"Refractive Index", "residual solvents", "SLES", "SLS", "TVOC", "Tween80", "UPLC-DAD", "VOC",
						"Afps", "ASTM D7339", "ASTM D7823", "ASTMD8133", "EPA 5021A", "EPA 8100", "MS 300-55", "REACH",
						// 추가
						"발효", "악취", "향기", "글루타민", "글루탐산", "글리신", "기능 성분", "기능성분", "나트륨", "녹농균", "농산물", "농약", "당류", "대두",
						"대장균", "라이신", "로이신", "루테인", "리스테리아", "메티오닌", "몰리브덴", "무기질", "미생물", "발린", "베타글루칸", "비배당체",
						"비스페놀", "비타민", "살모넬라", "세균", "세린", "스쿠알렌", "시스틴", "식품", "아르기닌", "아미노산", "아스파라긴", "아스파르긴산",
						"안토시아노사이드", "알라닌", "열량", "영양", "오리자놀", "유기산", "의약품", "이소로이신", "이소플라본", "인지질", "조단백", "조사포닌",
						"조지방", "지방산", "진균", "진세노사이드", "카테킨", "코엔자임", "콘드로이친", "콜라겐", "콜레스테롤", "키토산", "탄수화물", "트랜스 지방",
						"트랜스지방", "트레오닌", "트립토판", "티로신", "페닐알라닌", "포스파티딜콜린", "포화 지방", "포화지방", "폴리페놀", "프롤린", "항산화성분",
						"항생", "화장품", "황색포도상구균", "히스티딘", "1,2-Benzenedicarboxylic acid", "amino acid", "Anthracene",
						"C7-rich", "Chrysene", "di-C6-8-branched alkyl esters",
						"di-C7-11-branched and linear alkyl esters", "fluoroanthene", "fructooligosaccharides",
						"Fructose", "Glucose", "hyaluronic acid", "Lactose", "Maltose", "mineral", "Naphthalene",
						"Perfluorooctane sulfonate", "Perfluorooctanoic acid", "perylene", "Phenanthrene",
						"polycyclic aromatic hydrocarbons", "pyrene", "Sucrose", "Vitamin", "Afps GS 2014",
						"CEN/TS 15968", "EPA 8100", "EPA 8315A", "IEC 62321-8", "JIS K 6721", "US EPA 3540C",
						"건강기능식품공전", "식품공전", "화장품공전", "재질", "주재질", "F-search", "Py-", "pyrolysis-",
						"vinyl acetate content", "첨가제", "열분해", "고무성분", "고무 성분", "플라스틱성분", "플라스틱 성분", "TCB"

				},
				// 8page
				{ "Abbe", "abrasion", "absorbance", "absorptivity", "AFM", "albedo", "APHA",
						"Arithmetical average roughness", "ASTM B117", "ASTM C1113", "ASTM D1003", "ASTM D1044",
						"ASTM D1204", "ASTM D1218", "ASTM D1331", "ASTM D1505", "ASTM D2024", "ASTM D2196",
						"ASTM D2457", "ASTM D2565", "ASTM D3012", "ASTM D3389", "ASTM D3835", "ASTM D3985",
						"ASTM D4060", "ASTM D4812", "ASTM D4935", "ASTM D523", "ASTM D5492", "ASTM D570", "ASTM D5946",
						"ASTM D635", "ASTM D6518", "ASTM D7490", "ASTM D95", "ASTM D971", "ASTM E1164", "ASTM F1249",
						"ASTM F1307", "ASTM G154", "ASTM G155", "ASTMD5963", "ISO 11443", "ISO 1431", "ISO 304",
						"ISO 3219", "ISO 4287", "ISO 4288", "ISO 4589", "ISO 6721", "ISO1183", "ISO62", "ISO6603",
						"ISO75", "KS B 0801", "KS B 0811", "KS C IEC 60068", "KS G5603", "KS K 0555", "KS K ISO 9237",
						"KS L 2110", "KS L ISO 7459", "KS M ISO 11357", "KS M ISO 16152", "KS M ISO 16474",
						"Atomic force microscope", "balance", "Blue-Light", "brookfield", "cloud point", "color",
						"complex viscosity", "Contact  mode", "contact angle", "Cross-cut", "D3364", "D4459", "D6290",
						"dimension", "DIN", "E1347", "E308", "Gardner", "gel time", "glossiness", "gross", "Haze",
						"humidity", "IACS%", "L,a,b", "Light transmittance", "limiting oxygen index", "optical density",
						"OTR", "Oxygen gas transmission rate", "oxygen permeability", "permeability", "plate method",
						"prism coupler", "profiler", "QUV", "rebound", "reflectance", "refractive index", "rheometer",
						"ring method", "Stress relaxation", "Surface Roughness", "Taber", "Tapping", "thickness",
						"thixotropy", "Transmittance", "UV protection", "UV-vis", "van der waals", "vibro",
						"viscometer", "visual observation", "water absorption", "Water Vapor Transmission Rate",
						"weather-O-meter", "whiteness index", "WVTR", "Xenon", "Yellow Index", "Yellowness index",
						"가드너", "가소성", "거칠기", "경화거동", "계면 장력", "계면장력", "광택", "광택율", "광학", "광학밀도", "굴절", "굴절률", "굴절율",
						"내광성", "내마모", "내식성", "내열", "내오존성", "내장재", "내절성", "내한성", "내후성", "두께", "레오미터", "마모", "무게",
						"무게 측정", "무게측정", "밀착력", "반데르발스", "반발탄성", "반사율", "발수 특성", "발수특성", "변색", "복소점도", "부식", "분광",
						"브룩필드", "산화안정", "상쇄간섭", "색도", "색상", "색차", "시야각", "신뢰성", "아베굴절계", "아베수", "알베도", "열 충격", "열충격",
						"염수", "요변성", "운점", "자외선", "장기 열화", "장기열화", "적분구", "전후 육안", "전후육안", "점도", "점탄성", "접촉각", "정전기",
						"제논" },
				// 9page
				{
						"가스 흡착", "가스흡착", "간이가속수명", "결정패턴", "결정화도", "광전자분광기", "광학밀도", "근적외선", "기공률", "기공율", "녹는점", "단면",
						"동적광산란", "동적산란", "동적 산란", "딜라토미터", "라만", "밀도", "밀도구배관", "박막", "박막두께", "반치폭", "분산안정성", "분해온도",
						"비선광도", "v비중", "비표면적", "산화유도", "손실탄성", "수은 흡착", "수은흡착", "시차주사열량", "열중량", "열중량감소", "열팽창계수",
						"원편광이색성", "유리전이", "융점", "이물질", "입경", "입도", "입자", "저장탄성", "적외선분광법", "전위", "제타", "제타포텐셜",
						"주사전자현미경", "진밀도", "초기산화", "탭밀도", "투과모드", "투과전자현미경", "편광계", "표면 분석", "표면분석", "핵자기공명",
						"현미경", "활성탄", "활성화에너지", "흡광", "Activation energy", "apparent density", "BET",
						"Brunauer Emmett Teller", "Bulk density", "CD", "circular dichroism",
						"Coefficient of Thermal Expansion", "comparison", "confocal", "CP milling",
						"Cross Section Polisher", "crystallization", "CTE", "defect", "degree of crystalinity",
						"degree of crystalization", "densimeter", "density", "density", "depth profile",
						"Differential Scanning Calorimeter", "Dilatometer", "dispersion stability", "DLS", "DMA", "DSC",
						"DSC", "DTA", "Dynamic light scattering", "Dynamic mechanical analyzer", "EDX",
						"Electron probe microanalysis", "Energy Dispersive X-ray", "EPMA", "FTIR", "FT-IR",
						"Gas Pycnometer", "gas pycnometer", "glass transition temperatur", "hydroxyapatite ca/P",
						"initial oxidation temp", "laser diffraction", "library", "loss modulus", "mapping",
						"melting point", "Micro IR", "Microscope", "molar absorption coefficient", "morphology", "mp",
						"NIR", "NMR", "Optical", "particle", "particle", "particle size", "pentafluorophenol", "PFP",
						"polarimeter", "pore", "porosity", "PSA", "Raman",
						"Scanning Electron Microscope", "SEM", "specific gravity", "Spectroscopy", "storage modulus",
						"tan delta", "tap density", "TEM", "TGA", "Thermogravimetric analysis", "TMA",
						"Transmission electron microscope", "turbiscan", "Water cluster",
						"XPS", "XRD", "XRD", "zeta", "λcut-off", "λmax", "ASTM D3452", "ASTM D6474", "ASTM 1505",
						"ASTM D1895B", "ASTM D2070", "ASTM D2734", "ASTM D2734", "ASTM D3418", "ASTM D3895",
						"ASTM D4065", "ASTM D5594", "ASTM D6370", "ASTM D7028", "ASTM D792", "ASTM E 228", "ASTM E1131",
						"ASTM E1252", "ASTM E1641", "ASTM E831", "ATR법", "D4440", "D5279", "E1356", "ICDD", "ICSD",
						"ISO 11127", "ISO 1172", "ISO 13099", "ISO 13319", "ISO 21461", "ISO 3953", "ISO 9160",
						"ISO 9277", "ISO13320", "ISO13779", "ISO22412", "K ISO 11359", "KBr법", "KS A 0094", "KS L1621",
						"KS M 0024", "KS M 0024", "KS M 0043", "KS M 1802", "KS M ISO 060", "KS M ISO 6721",
						"KS M ISO11357", "KS M ISO11358", "KS M ISO7270", "KS MISO 21870", "OECD 102", "OECD 109",
						"OECD 110", "OECD 118", "USP NF 36",
						// 추가
						 "기공", "수은압입", "표면", "원소", "회절", "OIT", "ATR-IR", "선팽창", "이물","표면 원소","원소", "IR"
				}};
		String to = "";
		String cc = "";
		String[] toAddrs = {/*행정*/"koptri@polymer.co.kr",/*2P*/"juyeon.kim@polymer.co.kr",/*3P*/ "joohee.jin@polymer.co.kr"
				, /*5P*/ "hyunseok.jang@polymer.co.kr",/*6P*/ "eunseok.kim@polymer.co.kr",/*7P*/ "jungmi.kim@polymer.co.kr"
				, /*8P*/ "eunyoung.kim@polymer.co.kr", /*9P*/ "eunjin.hwang@polymer.co.kr"};
		String[][] ccAddrs = {/*행정*/{"kihong.park@polymer.co.kr"},
				/*2P*/{"kihong.park@polymer.co.kr","koptri@polymer.co.kr","jungmi.kim@polymer.co.kr"},
				/*3P*/{"mikyoung.kim@polymer.co.kr","kihong.park@polymer.co.kr", "koptri@polymer.co.kr"},
				/*5P*/{"jihye.nam@polymer.co.kr","kihong.park@polymer.co.kr", "koptri@polymer.co.kr"},
				/*6P*/{"jihye.lee@polymer.co.kr","kihong.park@polymer.co.kr", "koptri@polymer.co.kr"},
				/*7P*/{"inyeong.park@polymer.co.kr","kihong.park@polymer.co.kr", "koptri@polymer.co.kr", "juan.an@polymer.co.kr"},
				/*8P*/{"minjung.kwon@polymer.co.kr","kihong.park@polymer.co.kr", "koptri@polymer.co.kr"},
				/*9P*/{"dayoung.yeom@polymer.co.kr","kihong.park@polymer.co.kr", "koptri@polymer.co.kr"}
		};
		for(int i=0;i<toAddrs.length;i++) {
			for(int j=0;j<emailKeyword[i].length;j++) {
				if(body.replaceAll(" " , "").toLowerCase().contains(emailKeyword[i][j].replaceAll(" " , "").toLowerCase()) ) {
					if(to.equals("") || to == null ) {
						to = toAddrs[i];
					}
					else if(!to.contains(toAddrs[i])){
						to = to+","+toAddrs[i];
					}
					for(int k=0; k<ccAddrs[i].length;k++) {
						if(cc.equals("") || cc == null ) {
							cc = ccAddrs[i][k];
						}
						else if(!cc.contains(ccAddrs[i][k])){
							cc = cc+","+ccAddrs[i][k];
						}
					}
					break;
				}
			}
		}
		if(StringUtil.isEmpty(to)) {
			to = emailAddr;
		}
		Map<String, String> email = new HashMap<String, String>();
		email.put("to", to);
		email.put("cc", cc);
		return email;
		}

}
