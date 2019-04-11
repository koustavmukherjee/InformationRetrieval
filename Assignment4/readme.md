### Steps to Run:
#### On Ubuntu 64 Bit platform:
##### Making Configuration Changes
1. Please ensure a Solr instance with the required set of configuration is running on a system to which this nodejs application can be connected to
2. If required change any configuration in "run.sh" file or "conf.json" file provided within solr-nodejs-client folder
The configuration property keys, default values and a description of what they do are provided in the table below:

|Property   |Default Value   | Description  |
| ------------ | ------------ | ------------ |
|SOLR_PROTOCOL   |http   |The protocol or scheme on which Solr is running for connecting to it   |
|SOLR_HOST   |localhost   |The IP address of Solr instance   |
|SOLR_PORT   |8983   |The port of Solr instance. It is just for the purposes of establishing a connection to Solr from "nodejs", not for launching a Solr instance on a different port.   |
|SOLR_CORE_NAME   |search_core   |The Solr core on which page ranking has been configured and is used for connect to it from nodejs   |
|SOLR_START   |0   |The starting offset of the result set, passed as a query parameter to Solr   |
|SOLR_ROWS   |10   |The maximum number of records to fetch at a time, used for pagination and passed as a query parameter to Solr   |
|PORT   |3000   | The port to launch "nodejs"  |
|PAGE_RANK_FILE_NAME   |pageRankFile   |The filename that contains the page ranks for the "Page Rank" based sorting scheme   |
|PAGE_RANK_ORDER   |desc   |Can either be asc or desc. The order of sorting the results either in ascending order or descending order of page rank values for the Page Rank based ranking scheme. For the set of queries in this assignment there are no overlaps while the results are sorted in descending order, however there are some overlaps when the results are sorted in ascending order   |
Please don't change any special characters in "run.sh" or "conf.json" files like quotation marks ("") or slashes (/), just change the values. Changing anything else might have an expected impact on the application
##### Launching the application
3.  After making the required configuration changes, run the script "run.sh" using the following command from a terminal while navigation to the location where the folder "solr-nodejs-client" resides
`sh run.sh` or `./run.sh`
4. Open a browser ("Chrome or "Firefox") and navigate to the location http://localhost:3000/ to launch the application. "3000" is the deafualt port, in case if the PORT configuration is changed on "run.sh" or "congig.json", please use the corresponding port to launch the application. 
5. Ensure you have a stable internet connectivity for downloading and installing all the node dependencies.  Most of the client side bower dependencies have been included within the *public/lib* folder, however just for the sake of sanity please ensure you are connected to the internet even while launching the application.