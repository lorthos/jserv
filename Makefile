package:
	mvn clean package

fast-package:
	mvn clean package -DskipTests

docker: fast-package
	docker build -t jserv .

run-docker: fast-package docker
	docker run -p 8080:8080 --rm -i -t jserv

run-local-jar: fast-package
	java -server -Xms512M -Xmx512M -XX:+UseG1GC -Djava.util.logging.config.file=src/main/resources/logging.properties -jar target/jserv.jar src/test/resources/server.properties

run-local-jar-verbose: fast-package
	java -server -Xms512M -Xmx512M -XX:+UseG1GC -Djava.util.logging.config.file=src/test/resources/logging-test.properties -jar target/jserv.jar src/test/resources/server.properties

run-local:
	mvn clean compile -DskipTests exec:java -Djava.util.logging.config.file=src/test/resources/logging-test.properties -Dexec.mainClass="com.lorthos.jserv.App" -Dexec.args="src/test/resources/server.properties"



### MANUAL TESTS

test1:
	curl -v http://localhost:8080/

test2:
	curl -v http://localhost:8080/SERVER_ROOT.txt

test3:
	curl -v http://localhost:8080/ http://localhost:8080/

loadtest1:
	~/appz/bombardier-darwin-amd64 -c 2 -n 100 http://localhost:8080

loadtest2:
	~/appz/bombardier-darwin-amd64 -c 20 -n 20000 http://localhost:8080

loadtest3:
	~/appz/bombardier-darwin-amd64 -c 20 -n 200000 http://localhost:8080/dir1/image1.png/

test-all: test1 test2 loadtest1 loadtest2 loadtest3


