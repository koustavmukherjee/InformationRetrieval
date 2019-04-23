const Tokenizer = require('sentence-tokenizer');
const tokenizer = new Tokenizer('Snippet');
const fs = require('fs');
const FuzzySearch = require('fuzzy-search');
const Q = require('q');
var highlighter = require('keyword-highlighter');

const generateSnippet = function(filepath, id, term) {
    const filename = filepath + id + '.txt';
    let deferred = Q.defer();
    let data = fs.readFileSync(filename, 'utf8');
    let text = data.replace(/^\s*/gm, '').split('\n');
    let sentences = [];
    for(let i = 0; i < text.length; i++) {
        let line = text[i];
        tokenizer.setEntry(line);
        let tokens = tokenizer.getSentences();
        for(let j = 0; j < tokens.length; j++)
            sentences.push(tokens[j]);
    }
    const searcher = new FuzzySearch(text, {sort: true});
    const search_results = searcher.search(term);
    const result = search_results[0];
    if(result)
        return highlighter(term, result);
    else
        return undefined;
};

module.exports = {
    generateSnippet: generateSnippet
};