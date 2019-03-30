module.exports = function(conf) {
  const express = require('express');
  const router = express.Router();
  const request = require('request');

  router.get('/search', function(req, res, next) {
    let is_lucene_based = true;
    if(req.query.lucene_based !== undefined) {
      is_lucene_based = req.query.lucene_based === 'true' ? true : false;
    }
    let core = conf.get('SOLR_LUCENE_RANKED_CORE_NAME');
    if(!is_lucene_based)
      core = conf.get('SOLR_PAGE_RANKED_CORE_NAME');
    const url = conf.get('SOLR_PROTOCOL') + "://" + conf.get('SOLR_HOST') + ':' +
                conf.get('SOLR_PORT') + '/solr/' + core + '/select';
    const query = req.query.query;
    request({uri: url, method: 'GET', qs:{q:query}}, function(error, response, body){
      res.status(200).send(body);
    })
  });
  return router;
};