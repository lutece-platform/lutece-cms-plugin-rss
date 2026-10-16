--
-- Dumping data for table rss_feed
--
INSERT INTO rss_feed (id_rss_feed,name,url,last_fetch_date,last_fetch_status,last_fetch_error,workgroup_key,is_active) VALUES 
 (1,'Apache Jakarta','http://jakarta.apache.org/site/rss.xml','2007-03-15 16:06:13',0,'','all',1),
 (2,'RSS Advisory board','http://feeds.rssboard.org/rssboard.xml','2007-03-15 16:06:13',0,'','all',0);

--
-- Dumping data for table rss_generation
--
INSERT INTO rss_generation (id_rss,id_portlet,name,description,state,date_update) VALUES 
 (1,29,'liste_acteurs.xml','Liste des acteurs locaux',0,'2007-03-15 16:05:17');


--
-- Dumping data for table rss_feed_parameter
--
INSERT INTO rss_feed_parameter(parameter_key,parameter_value) VALUES
 ('is_active','0');