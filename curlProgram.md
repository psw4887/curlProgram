scurl(simple-curl) 만들기

Java Socket을 이용하세요.

curl 프로그램이 있습니다. 이 프로그램과 유사하게 동작하는 simple-curl 을 작성합니다.

scurl 은 다음과 같이 동작합니다.

URL 을 입력 인자로 받아 요청을 보내고 응답을 화면에 출력합니다.

옵션으로 GET 외에 다른 method(HEAD, POST, PUT, DELETE) 로 요청할 수 있습니다.

POST, PUT 등의 메소드를 사용할 때엔 전송할 데이타를 지정할 수 있습니다.

기본적으로는 요청헤더와 응답헤더를 출력하지 않습니다만, 옵션에 따라 출력할 수 있도록 합니다.

응답의 ContentType 을 확인하여, "text/*", "application/json" 만 화면에 출력합니다.

POST, PUT 의 경우 -H 로 Content-Type 이 지정되지 않으면 application/x-www-form-urlencoded 을 기본 타입으로 사용합니다.
