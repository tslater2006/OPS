# OpenPplSoft TODO

* For enhanced tracefile verification, experiment with turning on additional PeopleCode trace options, such as Function parameters, Return values, etc.
* Find all uses of OPSStmt and OPSResultSet and ensure they are within try-with-resources block.
* Convert event field on PeopleCodeProg to enum from string.
* Replace nulls with java.uti.Optional.
* Figure out how student self service web services are implemented and what is required for OPS to run them.
* Replace uses of reduce() using default value and && with "allMatch" on Stream; likewise with situations that should use anyMatch().
* Once keylist generation complete emission has been added, add a boolean to TraceFileVerifier so that keylist generation matchers will only be checked if keylist generation region is active.
