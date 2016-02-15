## 1 Introduction
* this material applies to Java 7 update 40 and Java 8 initial release
* enable JVM flag: `-XX:+FlagName`
* disable JVM flag: `-XX:-FlagName`
* setting a parameter for a flag: `-XX:FlagName=something`
* flags have defaults which depend on platform and other command-line arguments to the JVM
* *ergonomics* - process of automatically tuning flags based on the environment
* classes of machines: "client" and "server" - depends on environment
* performance of Java code depends on:
  * writing good algorithms
  * writing less code (the more code the longer compilation will take, more classes to load, more garbage collecting etc.)
  * optimize code early: Donald Knuth "We should forget about small efficiencies, say about 97% of the time; premature optimization is the root of all evil." means to write clean straightforward code that is simple to read and understand. Don't change algorithm and design to more complicated before profiling the program and being sure that they are really needed. However avoid constructs that are know for bad performance. Every line of code involves a choice, and if there is a choice between two simple, straightforward ways of programming, choose the more performant one. Example: logger and string concatenation done even when logger will not write this string to file/console.
  * look at other components: database, application server, backend server etc.
  * optimize for common case:
    * use profiler and focus on operations taking the most time
    * consider more likely bugs first: performance bug in new code > configuration of machine > bug in JVM or operating system
    * optimize what is used the most by users

## 2 An approach to performance testing
* FIRST PRINCIPLE: testing should occure on real product in the way the product will be used
  * categories of code used to do performance testing:
    * microbenchmarks - a test designed to measure a small unit of performance, e.g. single method invocation;
      * not easy to implement properly - compiler aggresive optimizations can change code so much that we're not measuring the right code (or no code at all);
      * also microbenchmark code can change production code multithreading behaviour and produce false statistics;
      * microbenchmarks must include a warm-up period - otherwise they will measure compilation time instead of only code performance
      * input values should be precalculated because otherwise benchmark will measure data preparation and produce false statistics;
      * it's result depends on input values range; random range isn't always the case; on the other hand common case input values may give false overall performance info;
      * measuring nanoseconds periods - is it worth to optimize rarely used nanoseconds code? Limited times when microbenchmarks are useful.
    * nanobenchmarks - measured whole application in conjunction with external resources (LDAP, database etc.) it uses
      * complex systems behave differently when all parts work together in contrary to mocking these parts;
      * many aspects of the JVM are tuned by default to assume that all machine resources are available, but performance behaviour may be different when other applications also run on this machine; example: when app uses 40% of CPU at average it means that most of the time it uses 30% and when GC is on the CPU usage goes up to 100%. When there are other apps running on the same machine our app won't have CPU time capacity like when it was single and will perform measurably different;
      * testing an entire application is the only way to know how code will actually run.
    * mesobenchmarks - somewhere between; they do some real work but not testing the whole application; it's measuring isolated performance at modular or operational level; reasonable compromise (but not substitution) for testing the full application
* SECOND PRINCIPLE: performance testing involves various ways to look at the application's performance
  * elapsed time (batch) measurements - the simplest way of measuring performance is to see how long it takes to complete a certain task
    * application performance changes in time due to application warm-up, done by:
      * JIT
      * filling caches in Java application
      * filling caches in database application
      * filling caches in operating system
      * warm-up in other places
    * application warm-up may take lots of time (45 minutes or more for some application servers configurations) so performance during this time may be important for users
  * throughput measurements
    * usually reported as operations per second (OPS), transactions per second (TPS), requests per second (RPS)
    * in client-server testing the client has no think time - sends request immediately after receiving response
    * in client-server testing the client has to be fast enough to really test the server; amount of client work depends on number of threads and how much they're doing; throughput measurement client uses less threads than response time measurement
    * it's common that these tests report throughput and average response time; 500 OPS x 0.5 s performs better than 400 OPS x 0.3 s
    * typically measured after warm-up
  * response time tests - amount of time that elapses between the sending of a request from a client and the receipt of the response
    * difference between throughput measurements and response time tests is that client waits some time before sending next request (after previously receiving a response); this pause is "think time" - it mimics users activity better; throughput becomes more or less fixed;
    * can be reported as an average or 90th percentile
    * example load generator: Faban (fhb)
* THIRD PRINCIPLE: understand how tests results vary over time
  * good benchmarks never process the same set of data - some random behaviour is needed to mimic the real world: how to compare tests results? Is the difference due to a regression or random variation of test? Comparing averages may not give the real view of what's going on; the best can be done is to provide probability, e.g. with high probability these averages are the same
  * testing like this is called regression testing
    * baseline - original code under testing
    * specimen - new code under testing
  * probability is calculated as p-value from Student's t-test, e.g. for p-value=43%: "there is a 57% probability that the specimen differs from baseline, and the best estimate of that difference is 25%"
  * alpha-value - statistical significance; typically 0.1, means that result is statistically significant if baseline and specimen will be the same 10% of the time; other common values: 0.5 (95%) or 0.01 (99%)
  * test is considered statistically significant if p-value is is smaller than 1 - alpha-value; if not the test is inconclusive;
  * proper way to search for regressions in code is to determine a level of statistical significance and then use t-test to determine if specimen and baseline are different within that degree of statistical significance;
  * statistical significance doesn't mean statistical importance; which test is more important: 0,01% difference with p-value 99% or 10% difference with p-value 80%? Second one is probably more important.
  * usually statistically inconclusive is when there is not enough data
* FOURTH PRINCIPLE: performance testing should be part of development cycle
  * ideally test when code is checked into repository
  * testing should be 100% automated
  * everything should be measured: application, operating system, database
  * run on the target system

## 3 Toolbox
  * Operating system
    * CPU Usage
      * CPU usage value is an average over some time: 1 second, 5 seconds, 30 seconds
      * the goal is to keep CPU usage as high as possible for as short as possible; e.g. optimize program task from 50% CPU for 10 minutes to 100% CPU for 5 minutes
      * command: vmstat 1
      * when application isn't using 100% CPU this may be the cause:
        * blocked on synchronization
        * waiting for external resource, e.g. database response
        * nothing to do
        * for multi-threaded multi-CPU CPUs can be idle when there can be no thread in e.g. fixed sized pool to execute job (all threads hanging on external resources)
      * however sometimes you don't want for app to use whole CPU
      * CPU run queue: how many threads can be run (vmstat - first number)
        * queue length should less or equal to CPUs on UNIX systems
    * Disk Usage
      * if app is doing lots of IO than it will probably be a bottleneck because of:
        * writing data inefficiently (too little throughput)
        * writing too much data (too much throughput)
      * if app is not using too much disk then swapping is still a big threat for performance
         * keeping unused memory on disk is quite OK for GUI apps, but not so good for server apps
      * command: iostat -xm 5
    * Network Usage
      * similar to disk: problems due to sending data too ineficiently or too much data
      * typically eth interface is considered saturated when sustained utilization is over 40%
      * commands: netstat, nicstat - these tools show utilisation of particular network interface only, not the whole network
    * Java Monitoring Tools
      * tools available in JDK
        * jcmd - prints basic info for Java process
        * jconsole - basic JVM activities visualized on GUI
        * jhat - heap memory dumps reader
        * jmap - heap dumps
        * jinfo - view/modify system properties
        * jstack - dump the stack
        * jstat - info about GC and class-loading
        * jvisualvm - JVM monitoring tool with GUI
      * tuning flags
        * there are 600+ tuning flags for JVM
        * list all: java other_options -XX:+PrintFlagsFinal -version (values with colon are not default; changed by command line, indirectly by other options or by JVM calculated it ergonomically
        * list all: jinfo -flags process_id
        * turn off: jinfo -flag -PrintGCDetails process_id
        * show one: jinfo -flag PrintGCDetails
        * it works for flags marked as manageable
      * thread info
        * show threads stacks: jstack process_id
        * show threads stacks: jcmd process_id Thread.print
      * class info
        * jconsole (number of classes)
        * jstat (number of classes, info about class compilation)
      * GC analisys
        * jconsole (heap)
        * jcmd (allows GC operations)
        * jmap (heap info, create heap dump)
        * jstat (different views on what GC is doing)
      * heap dump postprocessing
        * jvisualvm
        * jhat
        * Eclipse Memory Analyzer Tool (3rd party tool)
      * profiling tools
        * it makes sense to use many tools (particulalry for sampling profilers)
        * sampling profiling
          * least amount of overhead
          * many types of sampling errors
          * some methods may not be seen by profilers (sample can be taken only when thread is in safepoint)
        * instrumented profiling
          * more intrusive - add custom code on bytecode level
          * yelds more informations than sampling profilers
          * alter performance characteristics - thats why they should be used to profile small sections of code
        * blocking methods
          * threads that are blocked must be examined why they are blocked
          * some profilers hide waiting methods
        * native profilers
          * visibility into JVM code and applicatino code
          * if native profiler shows hard CPU usage on GC it means that GC tuning has to be done
          * if native profiler shows significant time usage in compilation threads this is usually not affecting performance

## 4 Working with JIT compiler
  * Hot spot - section of application code that is frequently executed
  * JIT - "just in time", compilation occurs as the program is executed
  * C1 - client compiler; compiles sooner but produces less effective code than C2; option -client; ignored on 64-bit JVMs
  * C2 - server compiler; option -server or option -d64
  * tiered compilation - code is first compiled with C1 and as it becomes hot C2 starts compiling hot spots; -XX:+TieredCompilation; on Java 8 turned on by default
  * batch jobs
    * for jobs running at fixed amount of time use compiler that produces fastest code
    * tiered compiler is a reasonable default
  * long-running applications
    * choose C2 preferably with tiered compilation
  * 32-bit JVM:
    * limited to 4GB total process size
    * faster (5%-20%) and less footprint than 64-bit
  * code cache
    * memory for compiled code
    * if its filled JIT will not compile more code to native assemblies
    * it can be filled for client or tiered compilators; log warning appears:
`Java HotSpot(TM) 64-Bit Server VM warning: CodeCache is full.
Compiler has been disabled.
Java HotSpot(TM) 64-Bit Server VM warning: Try increasing the
code cache size using -XX:ReservedCodeCacheSize=`
    * this cache can be monitored by jconsole (Memory Pool Code Cache chart) and altered via `-XX:ReservedCodeCacheSize=N`
  * compilation thresholds
    * standard compilation occurs when counter of method execution plus counter of branched backed loops is enough (branch back - loop ended execution by itself or because of `continue` statement etc.)
    * OSR - on-stack replacement: for very long or never engind loops if counter is enough given loop is eligible for compilation and replaced "in fly" with compiled version
    * OSR trigger = (CompileThreshold * ((OnStackReplacePercentage - InterpreterProfilePercentage)/100))
    * changing thresholds can soon up compilation (too low may cause code to be not optimized enough though)
    * lookwarm methods - methods that never gets compiled because counters decrease over time
  * inspecting compilation process
    * flag: -XX:+PrintCompilation
      * legend: % - OSR, s - synchronized method, ! - method has an exception handler, b - blocking mode, n - wrapper for a native method, COMPILE SKIPPED: reason {code cache filled, concurrent classloading}
      * compilation ids may occure out of order because compilation takes place in threads and queue elements have priority
    * jstat -compiler pid_number
  * advanced - little reason to change
    * compilation threads
      * compilation occurs asynchronously for methods that are placed on the compilation queue
      * queue is not ordered: hot methods are compiled before other methods
    * inlining
      * most beneficial optimization
      * decision to make method inlined depends on how hot it is and how big it is
    * escapea analysis
      * most sophisticated optimization
      * can often introduce "bugs" in improperly synchronized code
  * deoptimization
    * undo previous optimizations because of they are no longer valid
    * usually after deoptimization code warms up quickly
    * under tiered compilation code is deoptimized when it was compiled by C1 and now has been compiled by C2
  * tiered compilation levels
    * 0: interpreted code
    * 1: simple C1
    * 2: limited C1
    * 3: full C1
    * 4: C2 compiled code

## 5 An Introduction to Garbage Collection
  * besides rewriting code, tuning GC has most impact on the performance
  * There are four main garbage collectors on JVM:
    * serial collector (single-CPU machines)
    * throughput (parallel) collector
    * CMS - concurrent collector (low-pause, but not pauseless! More CPU intesive)
    * G1 collector (low-pause, but not pauseless! More CPU intesive)
  * GC means that developer doesn't need to manage memory by himself, but sometimes GC tuning is needed (and this is a tradeoff)
  * GC performance depends on these factors:
    * finding unused objects,
    * making their memory available
    * compacting the heap (to be able to allocate bigger objects than biggest just free'd)
  * `stop-the-world` pauses - moments when all application threads are stopped (when GC is moving objects around application threads cannot access these objects)
  * Generational Garbage Collectors
    * heap is splitted into areas called `generations`
      * old (tenured)
      * young, which is further splitted into:
        * eden and survivor
    * all GCs use old and young generations
    * generations where created because there are many short living objects in applications
    * minor GC: when young is fills up, the GC will stop app threads and empty it (some objects will be discarded, some moved elsewhere)
    * having you generation gives performance benefit: GC must process only part of heap which is faster compared to complete heap processing (even minor GC occures more requently though)
    * minor GC moves objects from eden to survivor space (or old generation) which automatically compacts eden - another performance benefit
    * full GC for simpler algorithms: when old generation is getting full all app threads are stopped, unused objects are discarded and heap is compacted; most of the times this is a long process;
    * full GC for CMS/G1: finding unused objects computationally and don't stop app threads; old generation compacting also uses different approach
    * G1/CMS can also have long pauses - tuning is needed to avoid this
    * ALL GCs cause `stop-the-world` when cleaning young generation (which is usually quick)
    * GC selection for JavaEE application:
      * individual requests will be impacted by GC pauses - if minimizing this effect is needed the concurrent collector is the better choice
      * if average reponse time is more important, the throughput GC is more appropriate
      * avoiding long pauses costs more CPU usage
    * GC selection for batch applications:
      * if enough CPU is available using paralell GC will make the job finish faster
      * if CPU is limited extra CPU will make the job finish later
  * GC algorithms
    * The serial garbage collector
      * simplest one; runs on 32-bit JVMs and single-CPU machines
      * single thread to process heap
      * minor and major GC stops all application threads
      * full GC will compact old generation
      * -XX:+UseSerialGC
    * The throughput collector
      * default for 64-bit JVMs and multi-CPU machines
      * multiple threads to process heap (-XX:+UserParallelOldGC before jdk7u4)
      * much faster minor GC than serial
      * minor and major GC stops all application threads
      * full GC will compact old generation
      * -XX:+UseParallelGC -XX:+UseParallelOldGC
    * The CMS collector
      * minor GC uses multiple threads and stops all application threads
      * different algorithm for young generation collecting (-XX:+UseParNewGC)
      * full GC may pause application threads only for a very short period of time (background threads scan heap periodically and discard unused objects)
      * trade off: increased CPU usage (because of background GC threads)
      * if not enough CPU or too much fragmentation CMS reverts to the behaviur of serial collector
      * -XX:+UseConcMarkSweepGC -XX:+UseParNewGC
    * The G1 collector
      * design to process large (>4Gb) heaps with minimal pauses
      * young generation is collected (via multiple threads) by stopping all application threads
      * old generation is processed by many threads and almost doesn't cause application pauses
      * old generation is divided into regions so compacting is done via coping objects between these regions
      * heap much less likely to be fragmentet
      * trade off: increased CPU usage
      * -XX:+UseG1GC
  * Forcing GC via `System.gc()`
    * always triggers a full GC
    * most of the time useless as it's only shifting GC in time
    * forcing GC may make sense for small benchmarks before measuring code performance (code must be able to warm-up properly)
    * sometimes before heap dump
    * RMI does its distributed GC every one hour
  * Basic GC tuning
    * Sizing the heap
      * choosing a heap size is a matter of balance: too small makes too often GC, too big is not necessary either (GC pauses increase)
      * never specify heap bigger than physical memory (if bigger swapping occures e.x. while GC processing)
      * initial heap size: -XmsN
      * maximum heap size: -XmxN
      * there is no hard-and-fast rule how to size heap
      * a good rule of thumb is to size the heap so that is 30% occupied after a full GC
    * Sizing the generations
      * choosing size for young and old generations is a matter of balance: larger young generation will be collected less often, and fewer objects will be promoted into old generation; but smaller old generation will fill up faster causing more requent full GCs;
      * -XX:NewRatio=N - set the ratio of the young generation to the old generation (default 2)
      * -XX:NewSize=N - set the initial size of the young generation
      * -XX:MaxNewSize=N - set the maximum size of the young generation
      * -XmnN - shorthand for setting both NewSize and MaxNewSize to the same value
      * Initial Young Gen Size = Initial Heap Size / (1 + NewRatio)
      * when a heap size is fixed (by setting -Xms equal to -Xmx), it is usually preferable to use -Xmn to specify a fixed size for the young generation as well
      * if an application needs a dynamically sized heap and requires a larger (or smaller) young generation, then focus on setting the NewRatio value
  * Sizing permgen and metaspace
    * permgen = permanent generation; in Java 7; (-XX:PermSize=N, -XX:MaxPermSize=N)
    * metaspace - in Java 8; (-XX:MetaspaceSize=N; -XX:MaxMetaspaceSize=N)
    * not exactly the same, but for end-user they hold class-related data; does not hold the actual instances of classes
    * no maximum size by default
    * resizing these regions needs full GC (when there are a lot of full GCs during the startup of a program (as it is loading classes), it is often because permgen or metaspace is being resized, so increasing the initial size is a good idea to improve startup in that case)
    * Java 7 applications that define a lot of classes should increase the maximum size as well (application servers typically specify a maximum permgen size of 128 MB, 192 MB, or more)
    * data stored in permgen is not permanent
    * when application server deploy/redeploy old classloaders are unreferenced and full GC may occure because old classes has to be GC and permgen resized
    * jmap -permstat (Java 7) / jmap -clstats (Java 8)
    * For typical applications that do not load classes after startup, the initial size of this region can be based on its usage after all classes have been loaded. That will slightly speed up startup.
  * Controlling parallelism
    * -XX:ParallelGCThreads=N number of threads used for following operations:
      * collection of the young generation when using -XX:+UseParallelGC
      * collection of the old generation when using -XX:+UseParallelOldGC
      * collection of the young generation when using -XX:+UseParNewGC
      * collection of the young generation when using -XX:+UseG1GC
      * stop-the-world phases of CMS (though not full GCs)
      * stop-the-world phases of G1 (though not full GCs)
      * ParallelGCThreads = 8 + ((N - 8) * 5 / 8) and this is sometimes too much:
        * 8 CPU, 1 GB heap 8 threads is too much; 4-6 is OK
        * 128 CPU machine: 83 GC is also too much
        * when more than one JVM runs on a machine limiting threads is a good idea; 16 CPU and 4 JVMs: 4 threads per JVM is OK to prevent app and or GC threads fighting for resources
