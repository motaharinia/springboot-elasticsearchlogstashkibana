## Spring Boot Elasticsearch Logstash Kibana

- Elasticsearch is a NoSQL database that is based on the Lucene search engine.
- Logstash is a log pipeline tool that accepts inputs from various sources, executes different transformations, and exports the data to various targets. It is a dynamic data collection pipeline with an extensible plugin ecosystem and strong Elasticsearch synergy
- Kibana is a visualization UI layer that works on top of Elasticsearch.

further references:     
- https://piotrminkowski.com/2019/10/02/using-logstash-logging-spring-boot-starter-for-logging-with-spring-boot-and-logstash/
- https://www.javainuse.com/spring/springboot-microservice-elk
- https://dzone.com/articles/sprinkle-some-elk-on-your-spring-boot-logs
- https://www.youtube.com/watch?v=BhmpDtS7g28


### Project Descriptions:
steps:
- download Elasticsearch from "https://www.elastic.co/downloads/elasticsearch"
- Run the "elasticsearch-7.10.1/bin/elasticsearch.bat" using the command prompt(command prompt should remain opened). Elasticsearch can then be accessed at "http://localhost:9200"
- Download the latest version of kibana from "https://www.elastic.co/downloads/kibana"
- Modify the "kibana-7.10.1-windows-x86_64/config/kibana.yml" to point to the elasticsearch instance. In our case this will be 9200. So uncomment the following line in "kibana-7.10.1-windows-x86_64/config/kibana.yml":
  ```elasticsearch.hosts: ["http://localhost:9200"]```
- Run the "kibana-7.10.1-windows-x86_64/bin/kibana.bat" using the command prompt(command prompt should remain opened). kibana UI can then be accessed at "http://localhost:5601"
- Download the latest version of logstash from "https://www.elastic.co/downloads/logstash"
- Create a configuration file named "logstash-7.10.1/bin/logstash.conf". In further section we will be making the changes for this file and starting logstash.
```
input {
input {
    udp {
        port => "5000"
        type => syslog
        codec => json
    }
    tcp {
        port => "5000"
        type => syslog
        codec => json_lines
    }
    http {
        port => "5001"
        codec => "json"
    }
}
 
filter {
  #If log line contains tab character followed by 'at' then we will tag that entry as stacktrace
  if [message] =~ "\tat" {
    grok {
      match => ["message", "^(\tat)"]
      add_tag => ["stacktrace"]
    }
  }
 
}
 
output {
  stdout {
    codec => rubydebug
  }
  # Sending properly parsed log events to elasticsearch
  elasticsearch {
    hosts => ["localhost:9200"]
  }
}
```
- Run the "logstash-7.10.1/bin/logstash.bat -f logstash.conf" using the command prompt(command prompt should remain opened). logstash can then be accessed at "http://localhost:9600"
- Start the spring boot application. Logs will be generated in ElasticsearchLogstashKibana.log.
- goto "http://localhost:8080/member/info" and "http://localhost:8080/member/error" 
- go to kibana UI "http://localhost:5601" console- localhost and create an index pattern logstash-* [Left Menu > Stack Management > Index Patterns > Create index pattern]
- go to kibana UI "http://localhost:5601" console- localhost and create scripted fields(which defined in java application with kv) in logstash-* [Left Menu > Stack Management > Index Patterns > logstash-* > Scripted Fields > Add Scripted Field]
- go to kibana UI "http://localhost:5601" console- localhost and discover logs [Left Menu > Discover > Index Patterns > Create index pattern]

### IntellliJ IDEA Configurations:
- IntelijIDEA: Help -> Edit Custom Vm Options -> add these two line:
    - -Dfile.encoding=UTF-8
    - -Dconsole.encoding=UTF-8
- IntelijIDEA: File -> Settings -> Editor -> File Encodings-> Project Encoding: form "System default" to UTF-8. May be it affected somehow.
- IntelijIDEA: File -> Settings -> Editor -> General -> Code Completion -> check "show the documentation popup in 500 ms"
- IntelijIDEA: File -> Settings -> Editor -> General -> Auto Import -> check "Optimize imports on the fly (for current project)"
- IntelijIDEA: File -> Settings -> Editor -> Color Scheme -> Color Scheme Font -> Scheme: Default -> uncheck "Show only monospaced fonts" and set font to "Tahoma"
- IntelijIDEA: Run -> Edit Configuration -> Spring Boot -> XXXApplication -> Configuration -> Environment -> VM Options: -Dspring.profiles.active=dev
- IntelijIDEA: Run -> Edit Configuration -> Spring Boot -> XXXApplication -> Code Coverage -> Fix the package in include box

<hr/>
<a href="mailto:eng.motahari@gmail.com?"><img src="https://img.shields.io/badge/gmail-%23DD0031.svg?&style=for-the-badge&logo=gmail&logoColor=white"/></a>

