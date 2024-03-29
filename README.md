# oracle-code-tokyo-2019-demo

## Appserver
### Run appserver locally
You need to have Project Loom's ea JDK.

```
$ JAVA_HOME=/path/to/loom/jdk ./run_appserver.sh fiber
$ JAVA_HOME=/path/to/loom/jdk ./run_appserver.sh thread 400
```
Former is the script to run appserver with fiber as request handler thread.
Latter is the script to run appserver with thread as request handler thread.
2nd arg for thread version(in case of above: 400) is num of request handler threads.

We share most code of appserver in both fiber version and thread version.
The only difference is executor for request handling.
Thus all RPC is sync and request thread will be blocked at RPC(like image effect and `/remote/sleep`) in thread version.
Async RPC for thread version is TODO.

Image effect endpoints require imageserver which is developed by @skrb.
Appserver consider imageserver is online at 8000 port.
If you prefer another endpoint, you can configure it in `appserver/target/classes/effect-servers.local`.

### Access appserver
Access `http://localhost:20080/` for fiber version and `http://localhost:20081/` for thread version.
You can see images and you can get effected image once you clicked.

`/sleep` endpoint is sleep 500ms in request handler before respond.
`/remote/sleep` endpoint is kick `/sleep` endpoint of itself as RPC and wait it's done.
These endpoint can be useful for benchmark.

By default, prometheus endpoint is up at 60080 for appserver and 60081 for thread.

## Prometheus
Download prometheus from https://prometheus.io/.

```
$ PROM_PATH=/path/to/prometheus ./run_prometheus.sh
```

You can get the metrics on `localhost:9090`.

appserver's metrics start from `app_`.

## Tsung
Tsung is a benchmark tool.
http://tsung.erlang-projects.org/

There're some configs for tsung in `/tsung`

You can run it:
```
./run_tsung.sh tsung/fiber/remote/sleep
./run_tsung.sh tsung/thread/remote/sleep
```

The log will be in `/tsung/log`

You can visualize the log by `tsung_stats.pl`

For Linux,
```
cd tsung/log/<logdir>
/usr/lib/x86_64-linux-gnu/tsung/bin/tsung_stats.pl
xdg-open graph.html
```
