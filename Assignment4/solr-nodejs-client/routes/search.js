module.exports = function(conf) {
  const express = require('express');
  const router = express.Router();
  const request = require('request');

  router.get('/search', function(req, res, next) {
    let is_lucene_based = true;
    if(req.query.lucene_based !== undefined) {
      is_lucene_based = req.query.lucene_based === 'true' ? true : false;
    }
    const url = conf.get('SOLR_PROTOCOL') + "://" + conf.get('SOLR_HOST') + ':' +
                conf.get('SOLR_PORT') + '/solr/' + conf.get("SOLR_CORE_NAME") + '/select';
    const query = req.query.query;
    let start_record = 0;
    if(req.query.start)
      start_record = req.query.start;
    let rows = 10;
    if(req.query.rows)
      rows = req.query.rows;
    let qs = {q:query, start: start_record, rows: rows, wt: 'json', indent: 'true'};
    if(!is_lucene_based)
      qs.sort = 'pagerank desc';
    request({uri: url, method: 'GET', qs: qs}, function(error, response, body){
      res.status(200).send(body);
    })
  });
  return router;
};