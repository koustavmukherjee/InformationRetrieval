import networkx as nx
G = nx.read_edgelist("D:\\usc_courses\\Semester-4\\Assignments\\Assignment4\\data\\edgelist.txt", create_using=nx.DiGraph())
pr = nx.pagerank(G, alpha=0.85, personalization=None, max_iter=30, tol=1e-06, nstart=None, weight='weight',dangling=None)
file = open("D:\\usc_courses\\Semester-4\\Assignments\\Assignment4\\data\\external_pagerank.txt","w",newline="\r\n");
for key, value in pr.items():
	file.write('/home/koustav/shared/guardiannews/' + key + '=' + str(value) + '\n');
file.close();