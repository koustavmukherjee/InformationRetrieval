"use strict";
module.exports = function(dict) {
    const express = require('express');
    const router = express.Router();
    router.post('/', function(req, res, next) {
        let words = req.body.query.split(' ');
        if(words.length == 0) {
            res.status(200).send(dict.lucky(req.body.query));
        }
        else {
            let results = [];
            for (let i = 0; i < words.length; i++) {
                results[i] = dict.lucky(words[i]);
            }
            res.status(200).send(results.join(' '));
        }
    });
    return router;
};