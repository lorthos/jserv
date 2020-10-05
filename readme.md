# Jserv

A Java Http Server

## Building

You will need jdk8, maven and docker

    make package
    
will run tests and package the app

## Running inside Docker

    make run-docker
    
## Running on Host Machine

    make run-local
    
## Testing

Run unit and integration tests:

    mvn test

Run manual tests:

    make test1
    ...
    make loadtest1
    ...

Manual tests might require additional tools installed.

## TODO

- File IO is not async, serverTaskThreadCount can be tuned for performance


## Bug

2) Only inside docker


    [2020-10-04 12:59:06] [INFO   ] [pool-2-thread-1] Currently open connections: 20
    java.util.concurrent.ExecutionException: java.lang.NullPointerException
    	at java.util.concurrent.FutureTask.report(FutureTask.java:122)
    	at java.util.concurrent.FutureTask.get(FutureTask.java:192)
    	at com.lorthos.jserv.http.servetask.ServeTaskListener.run(ServeTaskListener.java:25)
    	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
    	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
    	at java.lang.Thread.run(Thread.java:748)
    Caused by: java.lang.NullPointerException

#### feature
- more keep alive testing
- should keep alive header decrement every request?
- cleanup everywhere

+ open connection killer
+ how keep alive closed?
+ keep-alive
+ fix app config env
+ return HEAD of binary properly
+ add fs support
+ open files after ab
+ handle methods
+ parse request
+ display / download png image or other binary files
+ serving to browser causes favicon errors and leaves many open connections

- etag, if modified


