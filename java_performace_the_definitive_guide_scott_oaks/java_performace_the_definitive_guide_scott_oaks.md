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
  * Adaptive sizing
    * adaptive sizing controls how the JVM alters the ratio of young generation to old generation within the heap
    * adaptive sizing should generally be kept enabled, since adjusting those generation sizes is how GC algorithms attempt to meet their pause time goals
    * for finely tuned heaps, adaptive sizing can be disabled for a small performance boost (-XX:-UseAdaptiveSizePolicy to false); adaptive sizing is also effectively turned off if the minimum and maximum heap sizes are set to the same value, and the initial and maximum sizes of the new generation are set to the same value
    * -XX:+PrintAdaptiveSizePolicy to see resizing in GC.log
  * GC tools
    * basic (not recommended log): -verbose:gc or -XX:+PrintGC
    * detailed log: -XX:+PrintGCDetails and -XX:+PrintGCTimeStamps or -XX:+PrintGCDateStamps
    * log to file: -Xloggc:filename ; rotation: -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=N -XX:GCLogFileSize=N
    * GC Historgram - tool to analyze GC.log file
    * jstat -options shows full list of options
    * jstat -gcutil process_id 1000 will print each second: show how much percent are generations filled and how long and how many GCs were there
    * GC logs are the key piece of data required to diagnose GC issues; they should be collected routinely (even on production servers).
  * Summary
    * For many applications, though, the only tuning required is to select the appropriate GC algorithm and, if needed, to increase the heap size of the application.
    * Adaptive sizing will then allow the JVM to autotune its behavior to provide good performance using the given heap.
    * More complex applications will require additional tuning, particularly for specific GC algorithms.
## 6 Garbage Collection Algorithms - TBD

## 7 Heap Memory Best Practises
  * Heap analysis
    * Knowing which objects are consuming memory is the first step in knowing which objects to optimize in your code.
    * Histograms are a quick and easy way to identify memory issues caused by creating too many objects of a certain type.
    * most of the time memory dumps contain only live objects (sometimes tools accomplish this by doing full GC)
    * tools measuring heap usually need some processing power and memory to work and generally aren't usefull doing measurement of a program execution
    * heap histograms are a way to look at the number of objects within an application without doing a full heap dump: jcmd 8998 GC.class_histogram (jcmd doesn't invoke full GC); still slow though and should be used during performance measurement steady state
    * heap histogram: jmap -histo process_id
    * heap histogram (with full GC): jmap -histo:live process_id
    * heap dump: jcmd process_id GC.heap_dump /path/to/heap_dump.hprof
    * heap dump: jmap -dump:live,file=/path/to/heap_dump.hprof process_id (live forces full GC, -all dumps read objects too)
    * retained memory of an object is the amount of memory that would be freed if the object itself were eligible to be collected; root object + referenced objects (but not referenced by other root objects)
    * shallow size of an object: size of the object itself (without objects that it has references to)
    * deep size: size of root object and all referenced objects
    * GC roots of a particular object: system objects that hold some static, global reference that refers (directly or indirectly) to the object in question; typically these come from the static variables of a class loaded on the system or bootstrap classpath. This includes the Thread class and all active threads; threads retain objects either through their threadlocal variables or through references via their target Runnable object (or, in the case of a subclass of the Thread class, any other references the subclass has)
    * in some cases, knowing the GC roots of a target object is helpful, but if the object has multiple references, it will have many GC roots; this doesn't really help; instead, it can be more fruitful to play detective and find the lowest point in the object graph where the target object is shared. This is done by examining the objects and their incoming references, and tracing those incoming references until the duplicate path is identified.
  * Out of memory errors
    * No native memory is available for the JVM
      * not related to heap at all; native problem
    * The permgen (in Java 7 and earlier) or metaspace (in Java 8) is out of memory.
      * program uses more classes than it can fit in the default perm space; increase perm space or there is classloader memory leak
      * classloader memory leak occures most often in Java EE app servers and it's because classloaders are not allowed to go out of scope so classes metadata cannot be collected; this is most likely app server error; to debug this situation, the heap dump analysis just described is quite helpful: in the histogram, find all the instances of the ClassLoader class, and trace their GC roots to see what is holding onto them
    * The Java heap itself is out of memory: the application has too many live objects for the given heap size
      * the app may need more heap or has a memory leak
      * auto heap dumps when out of memory occurs:
        * -XX:+HeapDumpOnOutOfMemoryError causes the JVM to create a heap dump whenever an out of memory error is thrown
        * -XX:HeapDumpPath=<path> specifies the location where the heap dump will be written; the default is java_pid<pid>.hprof in the application’s current working directory. The path can specify either a directory (in which case the default file name is used), or the name of the actual file to produce
        * -XX:+HeapDumpAfterFullGC generates a heap dump after running a full GC
        * -XX:+HeapDumpBeforeFullGC generates a heap dump before running a full GC
    * The JVM is spending too much time performing GC
      * thrown when all of the following conditions are met
        * the amount of time spent in full GCs exceeds the value specified by the -XX:GCTimeLimit=N flag. The default value is 98 (i.e., if 98% of the time is spent in GC).
        * the amount of memory reclaimed by a full GC is less than the value specified by the -XX:GCHeapFreeLimit=N flag. The default value for this is 2, meaning that if less than 2% of the heap is freed during the full GC, this condition is met.
        * the above two conditions have held true for five consecutive full GC cycles (that value is not tunable)
        * the value of the -XX:+UseGCOverheadLimit flag is true (which it is by default)
  * Using less memory
    * Reducing objects sizes
      * reducing object sizes can often improve the efficiency of GC
      * the size of an object is not always immediately apparent: objects are padded to fit on 8-byte boundaries, and object reference sizes are different between 32- and 64-bit JVMs
      * even null instance variables consume space within object classes
    * Lazy initialization
      * use lazy initialization only when the common code paths will leave variables uninitialized
      * lazy initialization of thread-safe code is unusual but can often piggyback on existing synchronization
      * use double-checked locking for lazy initialization of code using thread-safe objects
    * Immutable and cannonical objects
      * canonical version of the object is a singular representation of immutable object (e.g. Boolean.TRUE)
      * to canonicalize an object, create a map that stores the canonical version of the object. In order to prevent a memory leak, make sure that the objects in the map are weakly referenced.
      * eliminating duplicate copies of immutable objects via canonicalization can greatly decrease the amount of heap an application uses
    * String interning
      * applications that reuse the same strings a lot will benefit by interning those strings
      * applications that intern many strings may need to adjust the size of the string intern table (unless they are running on a 64-bit server JVM starting in Java 7u40)
  * Object Lifecycle Management
    * Object Reuse
      * it's not the best idea from GC point of view; long living objects are more expensive to process by GC than short living ones
      * the reason for reusing objects is that many objects are quite expensive to initialize, and reusing them is more efficient than the trade-off in increased GC time
      * in Java, object allocation is quite fast and inexpensive (and arguments against object reuse tend to focus on that part of the equation). Object initialization performance depends on the object. You should only consider reusing objects with a very high initialization cost, and only then if the cost of initializing those objects is one of the dominant operations in program
      * examples of reusing objects:
        * Thread pools
          * holding lots of objects reduces (sometimes quite drastically) the efficiency of GC
          * pools of objects are inevitably synchronized, and if the objects are frequently removed and replaced, the pool can have a lot of contention. The result is that access to the pool can become slower than initializing a new object
          * performance impact of pools can be beneficial: pools allow access to scarce resources to be throttled (too many threads run simultaneously, the CPUs will be overwhelmed and performance will degrade); this principle applies to remote system access as well, and is frequently seen with JDBC connections; if more JDBC connections are made to a database than it can handle, performance of the database will degrade
        * thread-local variables
          * much easier and less expensive to manage than objects in a pool
          * there cannot be any more saved objects than threads, and much of the time it ends up being the same number; no throttling here;
          * no synchronization since they can only be used within a single thread; the thread-local get() method is relatively fast
        * JDBC pools
          * database connections are expensive to initialize
        * EJB pools
          * can be expensive to initialize
        * Large arrays
          * default initialization for all individual elements
        * Native NIO buffers
          * allocating a direct java.nio.Buffer (that is, a buffer returned from calling the allocateDirect() method) is an expensive operation regardless of the size of the buffer. It is better to create one large buffer and manage the buffers from that by slicing off portions as required, and return them to be reused by future operations
        * Security classes
          * instances of MessageDigest, Signature, and other security algorithms are expensive to initialize. The Apache-based XML code uses thread-local variables to save these instances
        * String encoder and decoder objects
          * various classes in the JDK create and reuse these objects. For the most part, these are also soft references
        * StringBuilder helpers
          * the BigDecimal class reuses a StringBuilder object when calculating intermediate results
        * Random number generators
          * instances of either the Random and—especially—SecureRandom classes are expensive to seed
        * Names obtained from DNS lookups
          * network lookups are expensive
        * ZIP encoders and encoders
          * these are not particularly expensive to initialize. They are, however, quite expensive to free, because they rely on object finalization to ensure that the native memory they use is also freed
      * large object pools of generic classes will most certainly lead to more performance issues than they solve. Leave these techniques to classes that are expensive to initialize, and when the number of the reused objects will be small
  * Weak, Soft, and Other References
    * a reference (or object reference) is any kind of reference: strong, weak, soft, and so on. An ordinary instance variable that refers to an object is a strong reference
    * indefinite (soft, weak, phantom, and final) references alter the ordinary lifecycle of Java objects, allowing them to be reused in ways that may be more GC-friendly than pools or thread-local variables
    * referent Indefinite references work by embedding another reference (almost always a strong reference) within an instance of the indefinite reference class. The encapsulated object is called the referent
    * Weak references should be used when an application is interested in an object only if that object is strongly referenced elsewhere in the application
      * If the referent of the weak reference is freed while the weak reference itself is still in the young generation, the weak reference will be freed quickly (at the next minor GC)
      * If the referent remains around long enough for the weak reference to be promoted into the old generation, then the weak reference will not be freed until the next concurrent or full GC cycle
      * Weak references should be used when the referent in question will be used by several threads simultaneously
        * in addition to keeping a strong reference to the particular data in the first user’s HTTP session, it makes sense to keep a weak reference to that data in a global cache. Now the second user will be able to find the data—assuming that the first user has not logged out and cleared her session.
        * "Hey, as long as someone else is interested in this object, let me know where it is, but if they no longer need it, throw it away and I will re-create it myself."
    * Soft references hold onto objects for (possibly) long periods of time, providing a simple GC-friendly LRU cache
      * the referent must not be strongly referenced elsewhere. If the soft reference is the only remaining reference to its referent, the referent is freed during the next GC cycle only if the soft reference has not recently been accessed
      ```
      long ms = SoftRefLRUPolicyMSPerMB * AmountOfFreeMemoryInMB;
      if (now - last_access_to_reference > ms)
      free the reference
      ```
      where `-XX:SoftRefLRUPolicyMSPerMB=N` (default 1000 ms), second value is the amount of free memory in the heap (once the GC cycle has completed)
      * to reclaim soft references more frequently, decrease the value of the `SoftRefLRUPolicyMSPerMB` flag
      * a long-running application can consider raising that value if two conditions are met:
        * there is a lot of free heap available
        * the soft references are infrequently accessed
      * soft references work well when the number of objects is not too large. Otherwise, consider a more traditional object pool with a bounded size, implemented as an LRU cache
      * "Hey, try and keep this around as long as there is enough memory and as long as it seems that someone is occasionally accessing it."
    * Indefinite References and Collections
      * Collection classes are frequently the source of memory leaks in Java
      * collection class that holds indefinite references can be used: WeakHashMap and WeakIdentityMap, but be aware of:
        * indefinite references can have a negative effect on the garbage collector
        * class itself must periodically perform an operation to clear all the unreferenced data in the collection (i.e., that class is responsible for processing the reference queue of the indefinite references it stores).
    * Indefinite references consume their own memory and hold onto memory of other objects for long periods of time; they should be used sparingly
    * Finalizers and final references
      * every java class has `finalize()` method - you should almost never use this method as it's bad for functional reasons and performance reasons as well
      * When an object that has a finalize() method is allocated, the JVM allocates two objects: the object itself, and a Finalizer reference that uses the object as its referent.
      * Unfortunately, finalizers are unavoidable in certain circumstances. The JDK, for example, uses a finalizer in its classes that manipulate ZIP files, because opening a ZIP file uses some native code that allocates native memory. The finalizer can ensure that the `close()` method has been called, even if the developer forgets that.
  * Summary
    * fast Java programs depend crucially on memory management
    * tuning GC is important, but to obtain maximum performance, memory must be utilized effectively within applications
    * normal time/space tradeoff of programming can swing to a time/space-and-time trade-off: using too much space in the heap can make things slower by requiring more GC
    * judicious use of object pools, thread-local variables, indefinite references can vastly improve the performance of an application, but overuse of them can just as easily degrade performance
    * in limited quantities—when the number of objects in question is small and bounded—the use of these memory techniques can be quite effective

## 8 Native Memory Best Practices
  * The total of native (nonheap) and heap memory used by the JVM yields the total footprint of an application
  * The total footprint of the JVM has a significant effect on its performance, particularly if physical memory on the machine is constrained. Footprint is another aspect of performance tests that should be commonly monitored
  * to monitor footprint on UNIX-based systems: top, ps; on Windows: perfmon, VMMap
  * to minimize footprint used by JVM limit the amount of memory used by the following:
    * heap
    * thread stacks
    * code cache
    * direct byte buffers (`-XX:MaxDirectMemorySize=N`)
  * native NIO buffers
    * important from a performance perspective, since they allow native code and Java code to share data without copying it
    * `allocateDirect()` call which creates the buffer is expensive - reuse created buffers, each thread individually (thread-local variable) or use object pool
    * allocate one big buffer and use `slice()` to allocate a portion of this buffer (unwieldy when buffers aren't the same size and fragmentation can occure)
  * Native Memory Tracking
    * to enable: `-XX:NativeMemoryTracking=off|summary|detail`
    * to see: `% jcmd process_id VM.native_memory summary`
    * provides details about the native memory usage of the JVM. From an operating system perspective, that includes the JVM heap (which to the OS is just a section of native memory)
    * summary mode of NMT is sufficient for most analysis, and allows you to determine how much memory the JVM has committed (and what that memory is used for)
  * JVM Tunings for the Operating System
    * Large Pages
      * it's important to maximize the hit rate on TLB entries
      * Since each entry represents a page of memory, it is often advantageous to increase the page size used by an application.
      * `-XX:+UseLargePages`
      * Using large pages will usually measurably speed up applications. Long-running JVMs will almost always benefit by using large pages, particularly if they have large heaps.
      * Large page support must be explicitly enabled in most operating systems.
    * Compressed oops
      * "oop" stands for ordinary object pointer: oops are the handles the JVM uses as object references. When oops are only 32 bits long, they can reference only 4 GB of memory (232), which is why a 32-bit JVM is limited to a 4 GB heap size; when oops are 64 bits long, they can reference terabytes of memory.
      * JVM can use 35-bit oops (32 bits shifted left by 3 bits introducing three zeros); JVM can reference 32GB with these pointers
      * Compressed oops are enabled by default whenever they are most useful.
      * A 31 GB heap using compressed oops will often outperform slightly larger heaps that are too big to use compressed oops.

## 9 Threading and Synchronization Performance
  * Thread pools and thread executors
    * core pool size = minimum size
    * if load is increased into the bottleneck then performance will degrade significantly
    * self-tuning thread pools is difficult-thread pools have no full understanding of all aspects of environment, e.g. adding more threads to pool when work is pending is often bad thing to do
    * idle time should be planned in minutes to handle spike in load
    * good minimum size for pool is equal to expected average parallel threads
    * sizing a thread pool executor
      * SynchronousQueue - new tasks spawn new threads until max size, then tasks are rejected; good for managing small number of tasks
      * Unbound queues - tasks are never rejected (queue size is unlimited); executor will use at most the tasks specified by core (minimum)
      * Bounded queues - use complicated algorithm to determine when to start new thread;
    * when attempting to maximize performance specify that ThreadPoolExecutor has the same number of core and max threads and utilize a LinkedBlockingQueue to hold the pending tasks (if an unbounded task list is appropriate), or an ArrayBlockingQueue (if a bounded task list is appropriate). 
    * Thread pools are one case where object pooling is a good thing: threads are expensive to initialize, and a thread pool allows the number of threads on a system to be easily throttled.
    * Thread pools must be carefully tuned. Blindly adding new threads into a pool can, in some circumstances, have a detrimental effect on performance (when tasks are not only CPU-bounded).
    * Using simpler options for a ThreadPoolExecutor will usually provide the best (and most predictable) performance.
  * Costs of synchronization
    * Thread synchronization has two performance costs: it limits the scalability of an application, and there is a cost in obtaining locks.
    * The memory semantics of synchronization, CAS-based utilities, and the volatile keyword can have a large performance impact, particularly on large machines with many registers.
  * Avoiding synchronization
    * CAS-based utilities compared to traditional synchronization:
      * If access to a resource is uncontended, then CAS-based protection will be slightly faster than traditional synchronization (though no protection at all will be slightly faster still).
      * If access to a resource is lightly or moderately contended, CAS-based protection will be faster (often much faster) than traditional synchronization.
      * As access to the resource becomes heavily contended, traditional synchronization will at some point become the more efficient choice. In practice, this occurs only on very large machines running a large number of threads
      * CAS-based protection is not subject to contention when values are read and not written.
    * Avoiding contention for synchronized objects is a useful way to mitigate their performance impact.
    * Thread-local variables are never subject to contention; they are ideal for holding synchronized objects that don’t actually need to be shared between threads.
    * CAS-based utilities are a way to avoid traditional synchronization for objects that do need to be shared.
  * False sharing
    * false sharing is when CPU loads nearby variables (e.g. class fields) into cache line and each time the app is updating them, other cores have to invalidate these variables and pull data again from main memory; if threads use these nearby variables (even each thread uses different one), because they all fit inside the same cache line all of these variables will be invalidated always on every CORE
    * big performance loss on code that frequently modifies volatile variables or exits synchronized blocks
    * very difficult to detect; when a loop executes to long do a code inspection to search for patterns where false sharing can occur;
    * best way to avoid is to move data to local variables and store them later; sometimes padding can help to move variables to different cache lines; external tools (like Intel VTune) may help
    * Java 8 introduced @Contended annotation which prevents JVM automatic padding (by default this works only for inner JDK classes; to make it work for the rest of classes: enable -XX:-RestrictContented flag); to disable automatic padding at all: -XX:-EnableContended
  * Tuning Thread Stack Sizes
    * Thread stack sizes can be reduced on machines where memory is scarce.
    * Thread stack sizes can be reduced on 32-bit JVMs to allow the heap to consume slightly more memory within the 4 GB process size limit.
  * Biased locking
    * locks can be granted fairly (round-robin) or biased (towards last thread that used it)
    * biased may benefit because of higher probability that data still sits in cache
    * apps with thread pool may benefit from turning biased locking off: -XX:-UseBiasedLocking
  * Lock spinning
    * handling synchronized lock that is contended:
      * thread which waits for lock enters into busy lock (polls lock periodically); good for short locks
      * thread can be placed in a queue and wait for notification; good for long locks
      * -XX:+UseSpinning is NOT in use anymore
  * Thread priorities
    * each thread has developer defined priority, which is a hint for operating system how important particular thread is
    * these hints are not treated very seriously by OS - developer can't depend on the priority of thread to affect it's performance
    * if some tasks are more important than other there has to be some logic in code that will handle this (e.g. by assigning tasks to differenet thread pools)
  * Monitoring threads and locks
    * Basic visibility into the threads of a system provides an overview of the number of threads running.
    * For performance analysis, the important facet of thread visibility is when threads are blocked on a resource or on I/O.
    * Java Flight Recorder provides an easy way to examine the events that caused a thread to block.
    * jstack provides some level of visibility into the resources threads are blocked on.
  * Summary
    * there are relatively few flags to tweak (and those have limits on some OS'es)
    * good thread performance is about following best-practice guidelines for managing the number of threads and for limiting the effects of synchronization
    * With the help of appropriate profiling and lock analysis tools, applications can be examined and modified so that threading and locking issues do not negatively affect performance

## 10 Java Enterprise Edition Performance
  * Basic Web Container Performance
    * produce less output
    * produce less whitespace
    * combine CSS and JavaScript resources (single file downloads faster than many small ones)
    * compress the output (it takes more CPU but over WAN content is delivered much faster)
    * don't use dynamic JSP compilation
  * HTTP Session State
    * Session state can have a major impact on the performance of an application server.
    * To reduce the effect of session state on the garbage collector, keep as little data in the session state as possible, and expire the session as soon as possible.
    * Look into app server-specific tunings to move stale session data out of the heap.
    * When using high availability, make sure to configure the application server to replicate only attributes that have changed.
  * Tuning EJB Pools
    * EJB pools are a classic example of object pooling: they are effectively pooled because they can be expensive to initialize, and because there are relatively few of them.
    * EJB pools generally have a steady and maximum size. Both values should be tuned for a particular environment, but the steady size is more important to minimize long-term effects on the garbage collector.
  * Tuning EJB Caches
    * EJB caches are used only for stateful session beans while they are associated with an HTTP session.
    * EJB caches should be tuned large enough to prevent passivation (serialiazing the bean and saving on disk).
  * Local and remote instances
    * Remote interfaces are slower than remote ones, even within the same server
  * XML and JSON Processing
    * Like HTML data, programmatic data will greatly benefit from reducing whitespace and being compressed
    * parser vs marshaller:
      * parser provides pure data and it's up to application logic what to do next; examples of parsers: token parsers, pull parsers
      * marshallers use parsers to process data but they provide data representation that more complex programs can use in their logic; examples: document models, object representations
    * If a program needs to make one simple pass through the data, then simply using the fastest parser will suffice. Directly using a parser is also appropriate if the data is to be saved in a simple, application-defined structure.
    * Using a document model is more appropriate when the format of the data is important. If the format of the data must be preserved, then a document format is very easy: the data can be read into the document format, altered in some way, and then the document format can simply be written to a new data stream.
    * For ultimate flexibility, an object model provides Java-language level representation of the data. The data can be manipulated in the familiar terms of objects and their attributes. The added complexity in the marshalling is (mostly) transparent to the developer and may make that part of the application a little slower, but the productivity improvement in working with the code can offset that issue.
    * There are many ways for Java EE applications to process programmatic data. As these techniques provide more functionality to developers, the cost of the data processing itself will increase. Don’t let that dissuade you from choosing the right paradigm for handling the data in your application.
    * Choosing a Parser
      * Choosing the right parser can have a big impact on the performance of an application.
      * Push parsers tend to be faster than pull parsers.
      * The algorithm used to find the factory for a parser can be quite time-consuming; if possible, bypass the services implementation and specify a factory directly via a system property.
      * At any point in time, the winner of the fastest parser implementation race may be different. Seek out alternate parsers when appropriate.
    * XML Validation
      * When schema validation is functionally important, make sure to use it; just be aware that it will add a significant performance penalty to parsing the data.
      * Always reuse schemas to minimize the effect of validation.
    * Document models
      * DOM and JsonObject models of data are more powerful to work with than simple parsers, but the time to construct the model can be significant.
      * Filtering data out of the model will take even more time than constructing the default model, but that can still be worthwhile for long-lived or very large documents.
    * Java Object Models
      * For XML documents, producing Java objects via JAXB yields the simplest programming model for accessing and using the data.
      * Creating the JAXB objects will be more expensive than creating a DOM object model.
      * Writing out XML data from JAXB objects will be faster than writing out a DOM object.
    * Object serialization
      * Serialization of data, particularly within Java EE, can be a big performance bottleneck.
      * Marking instance variables transient will make serialization faster and reduce the amount of data to be transmitted. Both of those are usually big performance wins, unless re-creating the data on the receiver takes a very long time.
      * Other optimizations via the writeObject() and readObject() methods can significantly speed up serialization. Approach them with caution, since it is easy to make a mistake and introduce a subtle bug.
      * Compressing serialized data is often beneficial, even if the data will not travel across a slow network.
    * Sizing data transfers
      * sometimes it may be worth to return/transmit lots of unnecessary data for client because still it will be more efficient than returning responses for subsequent requests for particular data

## 11 Database Performance Best Practises
  * JDBC
    * where work is performed:
      * thin driver: it relies on database server to do more processing; fairly small footprint
      * thick driver: offloads work form database but requires more processing power and memory on Java side
    * JDBC types:
      * type 1: bridge between ODBC and JDBC; avoid due to generally bad performance
      * type 2: use native code; tend to be 'thick'
      * type 3: pure Java but designed for specific architecture (sometimes, though usually not, an application server)
      * type 4: pure Java; tend to be 'thin'
    * Spend time evaluating the best JDBC driver for the application
    * The best driver will often vary depending on the specific deployment. The same application may be better with one JDBC driver in one deployment and a different JDBC driver in a different deployment.
    * Prepared statements and statement pooling
      * reusing prepared statements will bring performance boost
      * Prepared statements must be pooled on a per-connection basis. Most JDBC drivers and Java EE frameworks can do this automatically.
      * Prepared statements can consume a significant amount of heap. The size of the statement pool must be carefully tuned to prevent GC issues from pooling too many very large objects.
    * JDBC connection pools
      * Connections are expensive objects to initialize; they are routinely pooled in Java—either in the JDBC driver itself, or within Java EE and JPA frameworks.
      * As with other object pools, it is important to tune the connection pool so it doesn’t adversely affect the garbage collector. In this case, it is also necessary to tune the connection pool so it doesn’t adversely affect the performance of the database itself.
    * Transactions
      * Transactions affect the speed of applications in two ways: transactions are expensive to commit, and the locking associated with transactions can prevent database scaling.
      * Those two effects are antagonistic: waiting too long to commit a transaction increases the amount of time that locks associated with the transaction are held. Especially for transactions using stricter semantics, the balance should be toward committing more frequently rather than holding the locks longer.
      * For fine-grained control of transactions in JDBC, use a default TRANSACTION_READ_UNCOMMITTED level and explicitly lock data as needed.
    * Result Set processing
      * applications processing lots of data should consider changing fetch size
      * There is a trade-off between loading too much data in the application (putting pressure on the garbage collector) and making frequent database calls to retrieve a set of data.
  * JPA
    * Transaction handling
      * Explicitly managing transaction boundaries with user-managed transactions can often improve the performance of an application.
      * The default Java EE programming model—a servlet or web service accessing JPA entities via EJBs—supports that model easily.
      * As an alternative, consider splitting JPA logic into separate methods depending on the transactional needs of the application.
    * Optimizing JPA writes
      * JPA applications, like JDBC applications, can benefit from limiting the number of write calls to the database (with the potential trade-off of holding transaction locks).
      * Statement caching can be achieved either at the JPA layer or the JDBC layer. Caching at the JDBC layer should be explored first.
      * Batching JPA updates can be done declaratively (in the persistence.xml file), or programmatically (by calling the flush() method).
    * Optimizing JPA reads
      * JPA can perform several optimizations to limit (or increase) the amount of data read in a single operation.
      * Large fields (e.g., BLOBs) that are not frequently used should be loaded lazily in a JPA entity.
      * When a relationship exists between JPA entities, the data for the related items can be loaded eagerly or lazily. The choice depends on the needs of the application.
      * When eagerly loading relationships, named queries can be used to issue a single SQL statement using a JOIN statement. Be aware that this affects the JPA cache; it is not always the best idea (as the next section discusses).
      * Reading data via named queries will often be faster than a regular query, since it is easier for the JPA implementation to use a PreparedStatement for named queries.
    * JPA caching
      * The JPA L2 cache will automatically cache entities for an application.
      * The L2 cache does not cache entities retrieved via queries. This means that in the long run it can be beneficial to avoid queries altogether.
      * Unless query caching is supported by the JPA implementation in use, using a JOIN query turns out to frequently have a negative performance effect, since it bypasses the L2 cache
  * Summary
    * Batch reads and writes as much as possible by configuring the JDBC or JPA configuration appropriately.
    * Optimize the SQL the application issues. For JDBC applications, this is a question of basic, standard SQL commands. For JPA applications, be sure to consider the involvement of the L2 cache.
    * Minimize locking where possible. Use optimistic locking when data is unlikely to be contended, and pessimistic locking when data is contended.
    * Make sure to use a prepared statement pool.
    * Make sure to use an appropriately sized connection pool.
    * Set an appropriate transaction scope: it should be as large as possible without negatively affecting the scalability of the application because of the locks held during the transaction.

## 12 JAVA SE API Tips
  * Buffered I/O
    * For file-based I/O using binary data, always use a BufferedInputStream or BufferedOutputStream to wrap the underlying file stream.
    * For file-based I/O using character (string) data, always wrap the underlying stream with a BufferedReader or BufferedWriter.
    * The streams returned from a socket (via the getInputStream() or getOutputStream() methods) operate in the same manner, and performing I/O a byte at a time over a socket is quite slow.
    * ByteArrayInputStream and ByteArrayOutputStream doesn't need buffering as they are buffers themselves.
    * example: ObjectOutputStream(ByteArrayOutputStream()) is slower than ObjectOutputStream(BufferedOutputStream(GZIPOutputStream(ByteArrayOutputStream()))).
    * I/O must be properly buffered also for internal operations like compression and string encoding.
  * Classloading
    * Java 7 introduced parallel-capable constructors.
    * In complex applications (particularly application servers) with multiple classloaders, making those classloaders parallel-capable can solve issues where they are bottlenecked on the system or bootclass classloader.
    * Applications that do a lot of classloading through a single classloader in a single thread may benefit from disabling the parallelcapable feature of Java 7.
  * Random Numbers
    * Java’s default Random class is expensive to initialize, but once initialized, it can be reused.
    * In multithreaded code, the ThreadLocalRandom class is preferred.
    * The SecureRandom class will show arbitrary, completely random performance. Performance tests on code using that class must be carefully planned.
  * JNI
    * JNI is not a solution to performance problems. Java code will almost always run faster than calling into native code.
    * When JNI is used, limit the number of calls from Java to C; crossing the JNI boundary is expensive.
    * JNI code that uses arrays or strings must pin those objects; limit the length of time they are pinned so that the garbage collector is not impacted.
  * Exceptions
    * Exceptions are not necessarily expensive to process, though they should be used only when appropriate.
    * The deeper the stack, the more expensive to process exceptions.
    * The JVM will optimize away the stack penalty for frequently created system exceptions.
  * Strings
    * one-line string concatenation has good performance (translated by javac into StringBuilder)
    * for multiple concatenations use StringBuilder
    * Strings are immutable so it's better to reuse String objects
    * String encoding (Charset.encode()/decode()) should be done on larger bufferes
  * Logging
    * GC logging should always ON - overhead is very small but logged info may be precious
    * log info numerically - minimize any data conversion; logs can always be postprocessed to convert data
    * keep balance between data and logging level
    * code should contain lots of logging to enable users to figure out what it does, but none of that should be enabled by default
    * don't forget to test for the logging level before calling the logger if the arguments to the logger require method calls or object allocation
  * Java Collections API
    * Carefully consider how collections will be accessed and choose the right type of synchronization for them. However, the penalty for uncontended access to a memory-protected collection (particularly one using CAS-based protections) is minimal; sometimes it is better to be safe than sorry.
    * Sizing of collections can have a large impact on performance: either slowing down the garbage collector if the collection is too large, or causing lots of copying and resizing if it is too small.
  * AggressiveOpts
    * The AggressiveOpts flag enables certain optimizations in base classes (BigDecimal, BigInteger, MutableBigDecimal, DecimalFormat, DigitalList, NumberFormat, HashMap, LinkedHashMap, TreeMap). For the most part, these classes are faster than their replacements, but they may have unexpected side effects.
    * These replacement classes have been removed in Java 8.
    * other minor tunings of the JVM:
      * AutoFill, DoEscapeAnalysis (better loop optimizations; default on 7u4+)
      * AutoBoxCacheMax set from default 128 to 20000
      * BiasedLockingStartupDelay is reduced from 2000 to 500
      * OptimizeStringConcat for more optimized StringBuilder creation (default on 7u4+)
  * Lambda and Anonymous Classloading
    * The choice between using a lambda or an anonymous class should be dictated by ease of programming, since there is no difference between their performance.
    * Lambdas are not implemented as classes (they're implemented as static methods invoked by special JDK helper classes), so one exception to that rule is in environments where classloading behavior is important; lambdas will be slightly faster in that case.
  * Stream and filter performance
    * Filters offer a very significant performance advantage by allowing processing to end in the middle of iterating through the data.
    * Even when the entire data set is processed, a single filter will slightly outperform an iterator.
    * Multiple filters have some overhead; make sure to write good filters.
  * The ForkJoinPool
    * The ForkJoinPool class should be used for recursive, divide-andconquer algorithms.
    * Make the effort to determine the best point at which the recursion of tasks in the algorithm should cease. Creating too many tasks can hurt performance, but too few tasks will also hurt performance if the tasks do not take the same amount of time.
    * Features in Java 8 that use automatic parallelization will use a common instance of the ForkJoinPool class. You may need to adjust the default size of that common instance.
