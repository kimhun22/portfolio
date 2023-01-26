<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/jsp/common/_inc/_default.jsp" %>

<spring:eval var="sampleMaxFileCnt" expression="@appConfig.getProperty('globals.sample.maxFileCnt')"></spring:eval>
<script src="http://map.vworld.kr/js/vworldMapInit.js.do?version=2.0&apiKey=7FB63810-E8A0-3BD4-AF68-AB1993E591E2"></script>

<script src="/js/map/map.js"></script>
<style>

/**
스크롤 레이아웃 스타일 적용 2100907
*/
.frm{height: 50;}
</style>

<article class="at_1">
        <div class="search_div">
            <span>
                <select class='search_type'>
                    <option value="">장소명</option>
                    <option value="road">도로명</option>
                    <option value="parcel" selected>지번</option>
                </select>
            </span>
                <span><input type="text" id="search" placeholder="찾고자하는 것을 입력하세요."></span>
                <span><button class="search_btn">검색</button></span>
                <button type="button" onclick="javascript:setMode(vw.ol3.BasemapType.GRAPHIC);" >배경지도</button>
                <button type="button" onclick="javascript:setMode(vw.ol3.BasemapType.PHOTO);" >항공사진</button>
                <button type="button" onclick="javascript:setMode(vw.ol3.BasemapType.PHOTO_HYBRID);" >하이브리드</button>
  				건물 레이어 on/off<input type="checkbox" onclick="javascript:setThemeLayer1()" checked="checked" >
  </div>
        </div>
        <Div id="markPopup" style="width:300px; height:100px;display:none;">

  			 <table class="table_t01 data_table td_left hovertd">
						<colgroup>
							<col width="15%"/>
							<col width="85%"/>
						</colgroup>
						<tr>
							<th>지번주소</th><td><input type=text" id="parcel" value=""></td>
						</tr>
						<tr>
							<th>도로명 주소</th><td><input type=text" id="road" value=""></td>
						</tr>
						<tr>
							<th>지가</th><td><input type=text" id="jiga" value=""></td>
						</tr>
						
					</table>

	</Div>
	<Div id="searchPopup" style="position: absolute; background-color:white-space; left:23px; top:54px; width:300px; height:100px; z-index:1; display:none;">

		<table class="table_t01 data_table td_left hovertd" id="searchAddr">
			<colgroup>
				<col width="20%" />
				<col width="90%" />
			</colgroup>
			<thead>
			<tr>
				<th>순서</th>
				<th>상세주소</th>
			<tr>
			</thead>
			<tbody>
			</tbody>
		</table>
		<!-- 페이징 -->
		<div class="board_bottom mt-4">
			<div class="paginate">
				<ui/>
			</div>
			<p class="board_count">
				[총 <strong class="fc_primary" id="searchCount"></strong>건]
			</p>
		</div>
	</Div>
 <div id="vMap" style="width:100%;height:500px;"></div>
 	<table class="table_t01 data_table td_left hovertd" id="coordinates">
			<colgroup>
				<col width="20%" />
				<col width="90%" />
			</colgroup>
			<thead>
			</thead>
			<tbody>
			<tr>
				<th>경위도</th><td colspan="3">x : <input type=text" id="coordinatesX">, y: <input type=text" id="coordinatesY"></td>
			</tr>
			</tbody>
		</table>
</div>
</article>

<script>
$(document).ready(function() {
	toolBarSet();
});

var vmap; // 기본 지도를 받을 변수
var markerLayer; // 마커 레이어를 받을 변수
var vector_layer; // 면적 레이아웃
var x, y; // 클릭한 위경도를 받을 변수
var key = "7FB63810-E8A0-3BD4-AF68-AB1993E591E2"; // 브이월드 인증키
var areaOutput = ""; // 면적
var titleName; // 마커에 표시될 검색방법 타이틀
var element = document.getElementById("markPopup");
var markerSource = new ol.source.Vector();
vw.ol3.CameraPosition.center = [14098164.630229998, 4423743.6312705735]; // 기본 지도 시작 중심 좌표 지정(당진시청)
vw.ol3.CameraPosition.zoom = 15; // 시작 Zoom 레벨
vw.ol3.MapOptions = {
	      basemapType: vw.ol3.BasemapType.GRAPHIC
	    , controlDensity: vw.ol3.DensityType.EMPTY
	    , interactionDensity: vw.ol3.DensityType.BASIC
	    , controlsAutoArrange: true
	    , homePosition: vw.ol3.CameraPosition
	    , initPosition: vw.ol3.CameraPosition
}; 

	vmap = new vw.ol3.Map("vMap", vw.ol3.MapOptions);
	if ($("#checkbox").prop("checked", true)) {
		setThemeLayer1("연속지적도", "LP_PA_CBND_BUBUN");
	}
	if ($("#checkbox").prop("checked", false)) {
		vmap.removeLayer(vector_layer);
	}
	//연속지적도 레리어 
	var themeLayer1;
	function setThemeLayer1(name, layerName) {
		if (themeLayer1 == null) {
			themeLayer1 = vmap.addNamedLayer(name, layerName);
			vmap.addLayer(themeLayer1);
		} else {
			if (themeLayer1.getVisible()) {
				themeLayer1.setVisible(false);
			} else {
				themeLayer1.setVisible(true);
			}
		}
	}

	//지도 검색
	$(".search_btn").on("click", function() {
		var pageNum = 1;
		search(pageNum);
	});//지도 검색
	//지도 클릭 이벤트
	$("#vMap").mousedown(
					function(event) {
						if (event.button == '2') {
							vmapClickEvent(event.delegateTarget.ol_lm.keydown[0].bindTo.focus_);
						}
					});
	document.addEventListener('contextmenu', function(e) {
		e.preventDefault(); // 원래 있던 오른쪽 마우스 이벤트를 무효화한다.
	});
</script>
<%@include file="/WEB-INF/jsp/common/_inc/_footer.jsp" %>
