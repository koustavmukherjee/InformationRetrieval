var textract = require('textract');
var extract = require('sentence-extractor').extract;
var FuzzySearch = require('fuzzy-search');
// var url = "https://www.theguardian.com/us"
var url = 'https://www.theguardian.com/us/film';
//var search_term = 'protested border patrol';
var search_term = 'Oscar 2019';
/*textract.fromUrl(url, function( error, text ) {
    const searcher = new FuzzySearch(extract(text),{sort: true});
    const result = searcher.search(search_term)[0];
    console.log(result);
});*/
var Tokenizer = require('sentence-tokenizer');
var tokenizer = new Tokenizer('Chuck');

var fs = require('fs'), filename = 'D:\\usc_courses\\Semester-4\\Assignments\\Assignment4\\data\\big.txt\\000bad6a-6d34-4049-b2f9-5d737ba29003.html.txt';
fs.readFile(filename, 'utf8', function(err, data) {
    if (err) throw err;
    console.log('OK: ' + filename);
    var text = data.replace(/^\s*/gm, '').split('\n');
    var sentences = [];
    for(var i = 0; i < text.length; i++) {
        var line = text[i];
        tokenizer.setEntry(line);
        var tokens = tokenizer.getSentences();
        for(var j = 0; j < tokens.length; j++)
            sentences.push(tokens[j]);
    }
    const searcher = new FuzzySearch(sentences,{sort: true});
    const result = searcher.search('cache of weapons')[0];
    console.log(result);
});