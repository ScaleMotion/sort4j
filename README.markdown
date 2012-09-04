# About

sort4j is a sorting library for java. Unlike Collections.sort() and Arrays.sort() it provides ability of sorting huge amount of data
that doesn't fit into RAM. Currently, merge sort is implemented (small chunks is being sorted in memory, cached to disk and merged afterwards).

Hadoop backed sorting coming soon.

# Getting sort4j


* Direct jars downloads: [sort4j](http://vklimontovich.github.com/maven2/sort4j/sort4j/) (all available versions, use latest non snapshot one from there).
* Plugin a maven repository to pom.xml

        <repository>
            <id>scalemotion-open</id>
            <url>http://maven.scalemotion.com/open/</url>
        </repository>

and use it as dependency

        <dependency>
            <groupId>com.scalemotion.sort4j</groupId>
            <artifactId>sort4j</artifactId>
            <version>1.1</version>
        </dependency>

# Usage

The interface is quite straitforward:

* Create an instance of SortingTask
* By calling SortingTask.inputFile(...), SortingTask.outputFile(...), SortingTask.inputFormat(...), SortingTask.outputFormat(...), SortingTask.memoryCalculator(...)
define the location and format of output data. Consult SortingTask class javadoc for detailed instructions
* new MergeSorter(...).sort(sortingTask) for sorting. See MergeSorter javadoc for MergeSorter settings and constructor parameters.


# Example

        SortingTask<String> task = new SortingTask<String>()
                .inputFile("input.txt")
                .outputFile("output.txt")
                .inputFormat(new TextInputFormat())
                .outputFormat(new TextOutputFormat())
                .memoryCalculator(new StringMemoryCalculator());
        new MergeSorter<String>(tmp.getAbsolutePath(), 
                                2 /* number of working threads */, 
                                20000000 /* maximum RAM buffer size in bytes */)
                               .sort(task);


.