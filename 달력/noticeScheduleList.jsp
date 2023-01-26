<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/jsp/common/_inc/_default.jsp" %>

<spring:eval var="sampleMaxFileCnt" expression="@appConfig.getProperty('globals.sample.maxFileCnt')"></spring:eval>
<style>
/* section calendar */
.sec_cal {
    width: 360px;
    margin: 0 auto;
    font-family: "NotoSansR";
}

.sec_cal .cal_nav {
    display: flex;
    justify-content: center;
    align-items: center;
    font-weight: 700;
    font-size: 48px;
    line-height: 78px;
}

.sec_cal .cal_nav .year-month {
    width: 300px;
    text-align: center;
    line-height: 1;
}

.sec_cal .cal_nav .nav {
    display: flex;
    border: 1px solid #333333;
    border-radius: 5px;
}

.sec_cal .cal_nav .go-prev,
.sec_cal .cal_nav .go-next {
    display: block;
    width: 50px;
    height: 78px;
    font-size: 0;
    display: flex;
    justify-content: center;
    align-items: center;
}

.sec_cal .cal_nav .go-prev::before,
.sec_cal .cal_nav .go-next::before {
    content: "";
    display: block;
    width: 20px;
    height: 20px;
    border: 3px solid #000;
    border-width: 3px 3px 0 0;
    transition: border 0.1s;
}

.sec_cal .cal_nav .go-prev:hover::before,
.sec_cal .cal_nav .go-next:hover::before {
    border-color: #ed2a61;
}

.sec_cal .cal_nav .go-prev::before {
    transform: rotate(-135deg);
}

.sec_cal .cal_nav .go-next::before {
    transform: rotate(45deg);
}

.sec_cal .cal_wrap {
    padding-top: 40px;
    position: relative;
    margin: 0 auto;
}

.sec_cal .cal_wrap .days {
    display: flex;
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid #ddd;
}

.sec_cal .cal_wrap::after {
    top: 368px;
}

.sec_cal .cal_wrap .day {
    display:flex;
    align-items: center;
    justify-content: center;
    width: calc(100% / 7);
    text-align: left;
    color: #999;
    font-size: 12px;
    text-align: center;
    border-radius:5px
}

.current.today {background: rgb(242 242 242);}

.sec_cal .cal_wrap .dates {
    display: flex;
    flex-flow: wrap;
    height: 290px;
}

.sec_cal .cal_wrap .day:nth-child(7n) {
    color: #3c6ffa;
}

.sec_cal .cal_wrap .day:nth-child(7n+1) {
    color: #ed2a61;
}

.sec_cal .cal_wrap .day.disable {
    color: #ddd;
}

</style>

<article class="at_1">
<select class='search_year'>
 <c:forEach var="cnt" begin="1990" end="2030">
      <option value="${cnt}">${cnt}년</option>
 </c:forEach> 
    </select>
<select class='search_month'>
 <c:forEach var="cnt" begin="1" end="12">
 	<c:choose>  
		<c:when test="${cnt >= 10}"> 
			<option value="${cnt}">${cnt}월</option>
	</c:when> 
	<c:otherwise> 
		<option value="0${cnt}">0${cnt}월</option>
	</c:otherwise> 
</c:choose>
 </c:forEach> 
</select>
<span><button class="search_btn">검색</button></span>

      <div class="sec_cal">
  <div class="cal_nav">
    <a href="javascript:;" class="nav-btn go-prev">prev</a>
    <div class="year-month"></div>
    <a href="javascript:;" class="nav-btn go-next">next</a>
  </div>
  <div class="cal_wrap">
    <div class="days">
	  <div class="day">일</div>
      <div class="day">월</div>
      <div class="day">화</div>
      <div class="day">수</div>
      <div class="day">목</div>
      <div class="day">금</div>
      <div class="day">토</div>
    </div>
    <div class="dates"></div>
  </div>
</div>
</article>
<script>
var qwer = "${list1.nttDe}";
var currentYear ;
var currentMonth;
var currentDate;
var thisMonth = new Date();
var today;
$(document).ready(function() {
	calendarInit(null);
});
// 이전달로 이동
$('.go-prev').on('click', function() {
    thisMonth = new Date(currentYear, currentMonth - 1, 1);
    scheduleDelete();
    renderCalender(thisMonth);
});

// 다음달로 이동
$('.go-next').on('click', function() {
    thisMonth = new Date(currentYear, currentMonth + 1, 1);
    scheduleDelete();    
    renderCalender(thisMonth); 
 });
//지도 검색
$(".search_btn").on("click", function() {
	var year = $(".search_year option:selected").val();
	console.log(year);
	var month = $(".search_month option:selected").val();
	thisMonth = new Date(year, month-1, 1);
	renderCalender(thisMonth);
});//지도 검색
/*
    달력 렌더링 할 때 필요한 정보 목록 

    현재 월(초기값 : 현재 시간)
    금월 마지막일 날짜와 요일
    전월 마지막일 날짜와 요일
*/

function calendarInit(day) {
	  // 날짜 정보 가져오기
    var date = new Date(); // 현재 날짜(로컬 기준) 가져오기
    var uct = date.getTime() + (date.getTimezoneOffset() * 60 * 1000); // uct 표준시 도출
    var kstGap = 9 * 60 * 60 * 1000; // 한국 kst 기준시간 더하기
    today = new Date(uct + kstGap); // 한국 시간으로 date 객체 만들기(오늘)
	if(day ==  null){
	    thisMonth = new Date(today.getFullYear(), today.getMonth(), today.getDate());
	    // 달력에서 표기하는 날짜 객체
	    currentYear = thisMonth.getFullYear(); // 달력에서 표기하는 연
	    currentMonth = thisMonth.getMonth(); // 달력에서 표기하는 월
	    currentDate = thisMonth.getDate(); // 달력에서 표기하는 일
	    // kst 기준 현재시간
	    // console.log(thisMonth);
	    // 캘린더 렌더링
	}else{
		thisMonth = new Date(day.substring(0,4), parseInt(day.substring(4,6))-1, 1)
	}
    renderCalender(thisMonth);
}
function renderCalender(thisMonth) {
    //렌더링을 위한 데이터 정리
    currentYear = thisMonth.getFullYear();
    currentMonth = thisMonth.getMonth();
   	currentDate = thisMonth.getDate();
   	$(".search_year").val(currentYear).prop("selected", true);
   	if(parseInt(currentMonth)+1>=10){
       	$(".search_month").val(parseInt(currentMonth)+1).prop("selected", true);
   	}else{
   		var month= parseInt(currentMonth)+1;
   	    month = month >= 10 ? month : '0' + month;
   		$(".search_month").val(month).prop("selected", true);
   	}
    // 이전 달의 마지막 날 날짜와 요일 구하기
    var startDay = new Date(currentYear, currentMonth, 0);
    var prevDate = startDay.getDate();
    var prevDay = startDay.getDay();
    // 이번 달의 마지막날 날짜와 요일 구하기
    var endDay = new Date(currentYear, currentMonth, 0);
    var nextDate = endDay.getDate();
    var nextDay = endDay.getDay();
    //console.log(prevDate, prevDay, nextDate, nextDay);
    // 현재 월 표기
    $('.year-month').text(currentYear + '.' + (parseInt(currentMonth)+1));
    // 렌더링 html 요소 생성
    calendar = document.querySelector('.dates')
    calendar.innerHTML = '';
    // 지난달
    for (var i = prevDate - prevDay; i <= prevDate; i++) {
        calendar.innerHTML = calendar.innerHTML + '<div class="day prev disable" value='+i+'>' + i + '</div>'
    }
    // 이번달
    for (var i = 1; i <= nextDate; i++) {
		 calendar.innerHTML = calendar.innerHTML + '<div class="day current" id="day'+i+'">' + i + '</div>'
    }
    // 다음달
    for (var i = 1; i <= (7 - nextDay == 7 ? 0 : 7 - nextDay); i++) {
        calendar.innerHTML = calendar.innerHTML + '<div class="day next disable" value='+i+'>' + i + '</div>'
    }
    // 오늘 날짜 표기
    if (today.getMonth() == currentMonth) {
        console.log(today.getMonth()+""+currentMonth);
        todayDate = today.getDate();
        var currentMonthDate = document.querySelectorAll('.dates .current');
        currentMonthDate[todayDate -1].classList.add('today');
    }
    var month = parseInt(thisMonth.getMonth())+1;
    month = month >= 10 ? month : '0' + month
    fn_selectSchedule(thisMonth.getFullYear()+String(month));
}
function addSchedule(day){
	day = day >= 10 ? day : '0' + day;
	var month = $('.year-month').text().substring(5);
    month = month >= 10 ? month : '0' + month
  	var year = $('.year-month').text().substring(0, 4); 
    alert(year+month+day);
}
function scheduleCheck (schedule) {
	var day = schedule.date.substring(6);
	const element =document.getElementById('day'+day);
	element.innerHTML += '<div id="schedule" onClick="fn_goView('+schedule.nttNo+')">'+schedule.content+'<div>';
}
//상세 이동
fn_goView = function(nttNo) {
	$(location).attr('href', '/board/notice/noticeView.do?nttNo='+nttNo);
};
//상세 이동
fn_selectSchedule = function(day) {
	$.ajax({
		type : "POST",
    	url: "<c:url value='/board/notice/noticeScheduleListSelect.do?month="+day+"'/>",
    	dataType : "JSON",
		processData : false,
		contentType : false,
    	success: function(data) {
    		for(var o in data){
    	 		var event = {
    	 		  nttNo: data[o].nttNo,
    		 	  date: data[o].nttDe,
    			  type:  data[o].bbsKndCode,
    			  content:  data[o].nttSj
    			}; 
    			scheduleCheck(event);
    		}
    	}
    });
};

function scheduleDelete() {
	  $("#schedule").empty();
	} 
</script>