module.exports = function(conf) {
  const express = require('express');
  const router = express.Router();
  const request = require('request');

  router.get('/search', function(req, res, next) {
    const url = conf.get('SOLR_PROTOCOL') + "://" + conf.get('SOLR_HOST') + ':' +
                conf.get('SOLR_PORT') + '/solr/' + conf.get('SOLR_LUCENE_RANKED_CORE_NAME') + '/select';
    const query = req.query.query;
    request({uri: url, method: 'GET', qs:{q:query}}, function(error, response, body){
      res.status(200).send(body);
    })
  });
  return router;
};