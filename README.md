# sample-jep-graal

Basic compilation of Jep with Graal

## Prerequisites

Make sure that [graal](https://www.graalvm.org/) and [leiningen](https://leiningen.org/) are installed. Also make sure that the python installation is `v3+` If need be, the runtime python and pip commands can be set through ENV variables:

```
export JEP_PIP=pip3
export JEP_PYTHON=python3

```
[jep](https://github.com/ninia/jep) will be installed if not found.

## Building

The `prebuild.sh` command needs to be run once. It builds a jar and instruments it with `-agentlib:native-image-agent` attached.

```
$ preimage.sh
╰─$ ./prebuild.sh                                                                                                                               
BUILDING JAR
                                                               
INSTRUMENTING JAR
                                                              
.... <OUTPUTS> ....
                  
INSTRUMENTING COMPLETE

COPYING CONFIGS from ./log to ./native

PREIMAGE SUCCESS

```

Once the two files `jni-config.json` and `reflect-config.json` are generated, the native image can be built:

```
$ lein native-image

╰─$ lein native-image
[../jep.graal/target/jep-graal:92601]    classlist:   3,616.95 ms,  0.96 GB
[../jep.graal/target/jep-graal:92601]        (cap):   1,569.69 ms,  0.96 GB
[../jep.graal/target/jep-graal:92601]        setup:   3,552.43 ms,  0.96 GB

<ETC>

[../jep.graal/target/jep-graal:92601]        write:     697.21 ms,  2.61 GB
[../jep.graal/target/jep-graal:92601]      [total]:  70,857.87 ms,  2.61 GB
Created native image .../jep.graal/target/jep-graal

```

The binary is compiled to `target/jep-graal`.


## Running

Currently running the binary causes an error:

```
$ target/jep-graal

TEST EXAMPLE: a = 1 + 1, return a
Exception in thread "main" java.lang.UnsatisfiedLinkError: jdk.internal.loader.BootLoader.getSystemPackageNames()[Ljava/lang/String; [symbol: Java_jdk_internal_loader_BootLoader_getSystemPackageNames or Java_jdk_internal_loader_BootLoader_getSystemPackageNames__]
        at com.oracle.svm.jni.access.JNINativeLinkage.getOrFindEntryPoint(JNINativeLinkage.java:145)
        at com.oracle.svm.jni.JNIGeneratedMethodSupport.nativeCallAddress(JNIGeneratedMethodSupport.java:57)
        at jdk.internal.loader.BootLoader.getSystemPackageNames(BootLoader.java)
        at jdk.internal.loader.BootLoader.packages(BootLoader.java:192)
        at java.lang.ClassLoader.getPackages(ClassLoader.java:2354)
        at java.lang.Package.getPackages(Package.java:377)
        at jep.ClassList.loadPackages(ClassList.java:170)
        at jep.ClassList.<init>(ClassList.java:68)
        at jep.ClassList.getInstance(ClassList.java:366)
        at jep.Jep.setupJavaImportHook(Jep.java:320)
        at jep.Jep.configureInterpreter(Jep.java:310)
        at jep.SharedInterpreter.configureInterpreter(SharedInterpreter.java:64)
        at jep.Jep.<init>(Jep.java:282)
```

