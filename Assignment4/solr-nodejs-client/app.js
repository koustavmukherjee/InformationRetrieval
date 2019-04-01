const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');

const indexRouter = require('./routes/index');
const searchRouter = require('./routes/search');
const dataRouter = require('./routes/data');

const app = express();
const n_conf = require('nconf');

const csvFilePath='./URLtoHTML_guardian_news.csv'
const csv=require('csvtojson')

n_conf.argv()
    .env()
    .file({ file: './conf.json' });

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/solr', searchRouter(n_conf));

csv()
    .fromFile(csvFilePath)
    .then((jsonObj)=>{
        let csvToHtml = {};
        for(let i = 0;i < jsonObj.length; i++) {
            csvToHtml[jsonObj[i].filename] = jsonObj[i].URL;
        }
        app.use('/data', dataRouter(csvToHtml));
    });

module.exports = app;
