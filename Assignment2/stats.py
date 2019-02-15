import pandas as pd
from string import Template
import requests

fh = open("template/CrawlReport_Guardian_Template.txt", "r")
fh_out = open("template/CrawlReport_guardian.txt","w")

template = Template( fh.read() )


df_urls = pd.read_csv('output/urls_guardian.csv', skipinitialspace = True, quotechar = '"')
df_visit = pd.read_csv('output/visit_guardian.csv', skipinitialspace = True, quotechar = '"')
df_fetch = pd.read_csv('output/fetch_guardian.csv', skipinitialspace = True, quotechar = '"')

df_urls_unique = df_urls.drop_duplicates(['URL'])

name = 'Koustav Mukherjee'
uscid = '4813613054'
site_crawled = 'https://www.theguardian.com/us'
fetches_attempted = len(df_fetch.index)
fetches_succeeded = len(df_fetch[df_fetch['HTTP_STATUS'] == 200].index)
fetches_failed = len(df_fetch[df_fetch['HTTP_STATUS'] != 200].index)
total_urls_extracted = len(df_urls.index)
unique_urls_extracted = len(df_urls_unique.index)
unique_urls_within_site = len(df_urls_unique[df_urls_unique['LOCATION']=='OK'].index)
unique_urls_outside_site = len(df_urls_unique[df_urls_unique['LOCATION']=='N_OK'].index)

status_codes = df_fetch.groupby(['HTTP_STATUS']).size().to_dict()
status_code_list = ''

for status_code in status_codes:
	status_code_list += str(status_code) + ' ' + requests.status_codes._codes[status_code][0].upper() + ':' + str(status_codes[status_code]) + '\n'

size_bins = [0,1024,10240,102400,1048576,float("inf")]
labels = ['< 1KB','1KB ~ <10KB','10KB ~ <100KB','100KB ~ <1MB','>= 1MB']
sizes = pd.cut(df_visit['SIZE'], bins=size_bins, labels=labels).value_counts().to_dict()

file_size_list = ''
for label in labels:
	file_size_list += str(label) + ':' + str(sizes[label]) + '\n'
	
#pd.set_option('display.max_colwidth', 200)
#df_non_html = df_urls[df_urls['URL'].str.contains('|'.join(['gif','jpeg','png','pdf']))]
#df_non_html_ok = df_non_html[df_non_html['LOCATION'] == 'OK']

#df_visit_non_html = df_visit[df_visit['CONTENT_TYPE'].str != 'text/html']

content_types = df_visit.groupby(['CONTENT_TYPE']).size().to_dict()
content_type_list = ''
for content_type in content_types:
	content_type_list += content_type + ':' + str(content_types[content_type]) + '\n'

output_contents = template.safe_substitute({'name':name,'uscid':uscid,'site_crawled':site_crawled,'fetches_attempted':fetches_attempted,'fetches_succeeded':fetches_succeeded,'fetches_failed':fetches_failed,'total_urls_extracted':total_urls_extracted,'unique_urls_extracted':unique_urls_extracted,'unique_urls_within_site':unique_urls_within_site,'unique_urls_outside_site':unique_urls_outside_site,'status_code_list':status_code_list,'file_size_list':file_size_list,'content_type_list':content_type_list});

fh_out.write(output_contents)
fh_out.close()
fh.close()