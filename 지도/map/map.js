//vworld 지도 호출 및 인증키
//<script src="http://map.vworld.kr/js/vworldMapInit.js.do?version=2.0&apiKey=7FB63810-E8A0-3BD4-AF68-AB1993E591E2"></script>
//맵 
//<div id="vmap" style="width:100%;height:370px;left:0px;top:0px"></div>
//검색
/*
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
        </div>
*/
//지도 배경선택
/*
  <button type="button" onclick="javascript:setMode(vw.ol3.BasemapType.GRAPHIC);" >배경지도</button>
                <button type="button" onclick="javascript:setMode(vw.ol3.BasemapType.PHOTO);" >항공사진</button>
                <button type="button" onclick="javascript:setMode(vw.ol3.BasemapType.PHOTO_HYBRID);" >하이브리드</button>
*/
/*  팝업창 띄우기
<Div id="markPopup" style="position:absolute; background-color:white-space;left:500px; top:100px; width:300px; height:100px; z-index:1;display:none;">

  			 <table class="table_t01 data_table td_left hovertd">
						<colgroup>
							<col width="10%"/>
							<col width="10%"/>
							<col width="10%"/>
						</colgroup>
						<tr>
							<th>지번주소</th><td colspan="3"><input type=text" id="parcel"></td>
						</tr>
						<tr>
							<th>도로명 주소</th><td colspan="3"><input type=text" id="road"></td>
						</tr>
						
					</table>

	</Div>
	<Div id="searchPopup" style="position: absolute; background-color:white-space; left: 23px; top: 54px; width: 300px; height: 100px; z-index: 1; display: none;">

		<table class="table_t01 data_table td_left hovertd" id="searchAddr">
			<colgroup>
				<col width="20%" />
				<col width="90%" />
			</colgroup>
			<<thead>
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
*/
function toolBarSet(){
		/* vw.ol3.SiteAlignType = {
 	NONE : "none",
 	TOP_LEFT: "top-left",
 	TOP_CENTER : "top-center",
 	TOP_RIGHT : "top-right",
 	CENTER_LEFT: "center-left",
 	CENTER_CENTER: "center-center",
 	CENTER_RIGHT : "center-right",
 	BOTTOM_LEFT : "bottom-left",
 	BOTTOM_CENTER : "bottom-center",
 	BOTTOM_RIGHT : "bottom-right"
	}; */
	var options = {
  		map: vmap,
  		site: vw.ol3.SiteAlignType.TOP_RIGHT, //"top-left"
  		vertical: true,
  		collapsed: false,
  		collapsible: false,
		};


	var _toolBtnList = [
 		new vw.ol3.button.Init(vmap),
 	 	new vw.ol3.button.ZoomIn(vmap),
 		new vw.ol3.button.ZoomOut(vmap),
 		new vw.ol3.button.DragZoomIn(vmap),
 		new vw.ol3.button.DragZoomOut(vmap),
  		new vw.ol3.button.Pan(vmap),
 		new vw.ol3.button.Prev(vmap),
  		new vw.ol3.button.Next(vmap),
  		new vw.ol3.button.Full(vmap),
  		new vw.ol3.button.Distance(vmap),
 	 	new vw.ol3.button.Area(vmap),
	];

	var toolBar = new vw.ol3.control.Toolbar(options);
	toolBar.addToolButtons(_toolBtnList);
	vmap.addControl(toolBar);

}
function setMode(basemapType) {
  vmap.setBasemapType(basemapType);
}


function vmapClickEvent(event){
		// 마우스 커서 아래의 좌표값 구하기                    
            x = event[0];
            y = event[1];
            clickMap(x, y);     // addMarker함수에 위경도 변수 넘기고 실행
}
//머커 생성 및 좌표를 지번 주소로 변환
function clickMap(lon, lat) {
  // 마커 객체 상성
  markerLayer = new vw.ol3.layer.Marker(vmap);
  //  (비동기)좌표를 주소로 변환하는 api, 도로명주소 검색의 경우 건물의 geometry 기반으로 도로명 주소값을 가지고 오기 때문에
  //  건물 외의 것을 클릭하면 값이 없을 수 있습니다.
  //  브이월드 지도서비스에서 행정 주제도중 도로명주소건물 주제도에 해당 좌표값이 들어와야만 도로명 주소값을 리턴 받을 수 있습니다.
  $.ajax({
    type: "GET",
    url: "http://api.vworld.kr/req/address?",
    dataType: "jsonp", // CORS 문제로 인해 브이월드에선 jsonp를 사용한다고 함
    data: {
      service: "address",
      version: "2.0",
      request: "getaddress",
      format: "json", // 결과 포멧으로 xml 또는 json 타입으로 받아볼 수 있다.
      key: key, // 브이월드 인증키
      type: "both", // 검색 타입으로 '도로명:road' 또는 '지번:parcel' 또는 '둘다:both' 중 선택
      crs: "epsg:3857", // 브이월드 기본 좌표계
      point: lon + "," + lat, // 좌표
      zipcode: true, // 우편번호 여부
      simple: false, // 간략 결과 여부
    },
    success: function (json_data) {
		console.log(json_data);
      vectorLayer(json_data.response.input.point.x, json_data.response.input.point.y);
      if (json_data.response.status == "NOT_FOUND") {
        text = "검색 결과가 없습니다.";
      } else {
			coordinatesTransform(lon, lat);	// 좌표계 변경
		    popupCoordinate(lon, lat); // OverLay 생성 오른쪽 클릭 위치에 팝업 생성
     		if(json_data.response.result.length > 1){
				$("#road").val(json_data.response.result[1].text);
	 	  		$("#parcel").val(json_data.response.result[0].text);
			}
			else{
           		$("#road").val("");
	 	 		$("#parcel").val(json_data.response.result[0].text);
			}	
			document.getElementById("markPopup").style.display = "inline";				
      }
    },
    error: function (xtr, status, error) {
      alert(xtr + " : " + status + " : " + error);
    },
  });
}
// OverLay 생성 오른쪽 클릭 위치에 팝업 생성
function popupCoordinate(lon,lat) {
	var overlay = new ol.Overlay({
		        element: element, // 생성한 DIV
		        autoPan: true,
		        className: "multiPopup",
		        autoPanMargin: 100,
		        autoPanAnimation: {
		            duration: 250
		        }
		    });
	//오버레이의 위치 저장
	var coordinate =[lon, lat];
	overlay.setPosition(coordinate);
	//지도에 추가
	vmap.addOverlay(overlay);		
}
// 마커,팝업 생성 함수
function addMarker(lon, lat){                // 파라미터로 위도,경도  
		var img = document.createElement('img'); 
    img.src = 'http://map.vworld.kr/images/ol3/marker_blue.png'; 
  	var overlay = new ol.Overlay({
        element: img,
		autoPanAnimation: {
			duration: 250
		}
    });
	overlay.setPosition([lon, lat]);
	//지도에 추가
	vmap.addOverlay(overlay);
 }

//좌표 변경
function coordinatesTransform(lon, lat) {
	var coordinate = new ol.geom.Point([lon, lat]).transform('EPSG:3857', 'EPSG:4326');
	$("#coordinatesX").val(coordinate.flatCoordinates[0]);
	$("#coordinatesY").val(coordinate.flatCoordinates[1]);	
}
//레이어 설정
function vectorLayer(x, y) {
  var geojsonObject;
  $.ajax({
    type: "get",
    url: "http://api.vworld.kr/req/data",
    data: {
      key: key, //브이월드 키
      domain: "http://localhost:9090/main.do", //도메인 주소
      service: "data",
      version: "2.0",
      request: "getfeature",
      format: "json",
      data: "LP_PA_CBND_BUBUN", //연속지적도 서비스
      geomFilter: "POINT(" + x + " " + y + ")", // x,y 좌표
      geometry: "true",
      attribute: "true",
      crs: "EPSG:3857",
    },
    dataType: "jsonp",
    async: false,
    success: function (data) {
      vmap.removeLayer(vector_layer);
	  //레이어 좌표 배열
      geojsonObject = data.response.result.featureCollection.features[0].geometry;
      var polygon = new ol.geom.MultiPolygon([ geojsonObject.coordinates[0] ]); // polygon_feature 생성
	  var polygon_feature = new ol.Feature(polygon);
 	  var style = new ol.style.Style({
	  		stroke : new ol.style.Stroke({
    		color : [ 0, 255, 0, .7 ],
  	  		width : 3
		 	 }),
 			 fill : new ol.style.Fill({
	   		 color : [ 255, 0, 0, .4 ]
	  		})
		}); // 스타일 정의
	polygon_feature.setStyle(style); // 정의한 스타일을 적용
	vector_layer = new ol.layer.Vector({
	  source : new ol.source.Vector({
 	   features : [ polygon_feature ]
	  })
	})
      // 맵 폴리곤 레이어 등록
      vmap.addLayer(vector_layer);
      $("#jiga").val(data.response.result.featureCollection.features[0].properties.jiga+"("+data.response.result.featureCollection.features[0].properties.gosi_year+"년)");
    },
    beforesend: function () {},
    error: function (xhr, stat, err) {},
  });
}

//지도 검색
function search(page) {
	//검색어 빈값 확인
  if ($("#search").val() == "") {
		alert("검색어를 입력해주세요");
		return ;
  }
  //검색시 팝업창 초기화
  //document.getElementById("markPopup").style.display = "none";
  document.getElementById("searchPopup").style.display = "none";
  // 기존 마커, 레이어 설정 제거
  vmap.clear();
  // 마커 객체 상성
  markerLayer = new vw.ol3.layer.Marker(vmap);
  $("#searchAddr> tbody").empty();
  //검색 주소
  var contents_data;
  // 선택한 검색 종류 값
  var typeName = $(".search_type option:selected").val();
  var type = "address";
  //장소 검색설정
  if (typeName == "") {
    type = "place";
  }
  $.ajax({
    type: "get",
    url: "http://api.vworld.kr/req/search",
    data: {
      page: page,
      type: type, // 주소 검색방법
      category: typeName, // 도로명 : road, 지번 : parcel
      request: "search",
      apiKey: key, // 브이월드 지도 인증기
      domain: "http://localhost:9090/main.do", // 도메인 주소
      crs: "EPSG:3857", // 브이월드 좌표계
      query: $("#search").val(), // 사용자가 입력한 text
    },
    dataType: "jsonp",
    async: false,
    success: function (data) {
      if (data.response.status == "NOT_FOUND") {
        alert("검색결과가 없습니다.");
      } else {
        for (var o in data.response.result.items) {
          //검색 첫 주소 설정
          if (typeName == "road") {
            titleName = "도로명 주소";
            contents_data = data.response.result.items[o].address.road;
          } else if (typeName == "parcel") {
            titleName = "지번 주소";
            contents_data = data.response.result.items[o].address.parcel;
          } else if (typeName == "") {
            titleName = "지번 주소";
            contents_data =
              data.response.result.items[o].address.parcel +
              "<br>" +
              data.response.result.items[o].title;
          }
          var count = (parseInt(o) + 1) * page; // 순서
          //상세주소
          $("#searchAddr").append(
            "<tr onClick='javascript:move(" +
              data.response.result.items[o].point.x +
              "," +
              data.response.result.items[o].point.y +
              ");vectorLayer(" +
              data.response.result.items[o].point.x +
              "," +
              data.response.result.items[o].point.y +
              ");clickMap(" +
              data.response.result.items[o].point.x +
              "," +
              data.response.result.items[o].point.y +
              ");'><td>" +
              count +
              "</td><td>" +
              contents_data +
              "</td></tr>"
          );
          //총 건
          $("#searchCount").text(data.response.page.total);
          //검색창 보여주기
          document.getElementById("searchPopup").style.display = "inline";
          //검색한 10건 레이어
		  move(data.response.result.items[0].point.x,data.response.result.items[0].point.y)
		  addMarker(data.response.result.items[o].point.x, data.response.result.items[o].point.y);
        }
      }
    },
    complete: function () {
      vmap.addLayer(markerLayer); // 마커를 vmap에 등록
    },
    error: function (xhr, status, error) {
      console.log(xhr, status, error);
    },
  });
}
//좌표로 이동 및 줌
var move = function (x, y) {
  vmap.getView().setCenter([x, y]); // 지도 이동
  vmap.getView().setZoom(20); // 줌레벨 설정
};
