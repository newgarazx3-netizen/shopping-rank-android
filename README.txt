쇼핑 노출순위 Android Viewer v0.3 패치

교체 파일:
1) app/src/main/java/com/shoppingrank/viewer/MainActivity.java
2) app/build.gradle
3) 루트 build.gradle (안전 확인용, 이미 같으면 변경 불필요)

핵심 변경:
- Android 15 상태바/내비게이션바 안전 여백 강제 처리
- WebView <style> 주입 대신 각 DOM 요소에 직접 important 스타일 적용
- MutationObserver로 로그인/렌더링 후에도 모바일 앱 전용 레이아웃 재적용
- 검색 필터 2열: 수집일|오전오후, 플랫폼|키워드, 정렬방식|자사만보기
- 자사만 보기 체크박스 15px 고정
- 표시 모델 필터 입력/버튼 한 줄 압축
