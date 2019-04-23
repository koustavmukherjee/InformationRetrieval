"use strict";
module.exports = function(conf) {
  const express = require('express');
  const router = express.Router();
  const request = require('request');
  const snippet = require('../lib/snippet');
  const spell = require('../lib/spell');

  router.get('/search', function(req, res, next) {
    let is_lucene_based = true;
    if(req.query.lucene_based !== undefined) {
      is_lucene_based = req.query.lucene_based === 'true' ? true : false;
    }
    const url = conf.get('SOLR_PROTOCOL') + "://" + conf.get('SOLR_HOST') + ':' +
                conf.get('SOLR_PORT') + '/solr/' + conf.get("SOLR_CORE_NAME") + '/select';
    const query = req.query.query;
    let start_record = 0;
    if(conf.get('SOLR_START'))
      start_record = conf.get('SOLR_START');
    let rows = 10;
    if(conf.get('SOLR_ROWS'))
      rows = conf.get('SOLR_ROWS');
    let qs = {q:query, start: start_record, rows: rows, wt: 'json', indent: 'true'};
    if(!is_lucene_based)
      qs.sort = conf.get('PAGE_RANK_FILE_NAME') + ' ' + conf.get('PAGE_RANK_ORDER');
    request({uri: url, method: 'GET', qs: qs}, function(error, response, body){
      if(error) {
        res.status(500).send(error);
      }
      else {
        body = JSON.parse(body);
        let docs = body.response.docs;
        for(let i = 0; i < docs.length; i++) {
            let id = docs[i].id.split("/").pop();
            let snippet_text = snippet.generateSnippet(conf.get('SNIPPET_DATA_FILES'), id, query);
            body.response.docs[i].snippet = snippet_text;
        }
        let corrected_spell = spell.getCorrectedPhrase(query);
        if(query.toLowerCase() != corrected_spell)
          body.response.corrected_spell = corrected_spell;
        res.status(response.statusCode).send(body);
      }
    })
  });

  router.get('/suggest', function(req, res, next) {
    const url = conf.get('SOLR_PROTOCOL') + "://" + conf.get('SOLR_HOST') + ':' +
        conf.get('SOLR_PORT') + '/solr/' + conf.get("SOLR_CORE_NAME") + '/suggest';
    const query = req.query.query;
    let qs = {q:query};
    request({uri: url, method: 'GET', qs: qs}, function(error, response, body){
      if(error) {
        res.status(500).send(error);
      }
      else {
        res.status(response.statusCode).send(body);
      }
    })
  });
  return router;
};
