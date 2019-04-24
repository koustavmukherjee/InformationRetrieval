'use strict';

// Declare app level module which depends on views, and core components
let searchApp = angular.module('searchApp', ['ngSanitize']);

searchApp.config(['$locationProvider', function($locationProvider) {
  $locationProvider.hashPrefix('!');
}]);

searchApp.controller('main',function($scope, $http, initializationData){
  $scope.undefined = undefined;
  $scope.search_query = undefined;
  $scope.lucene_search_results = {};
  $scope.page_rank_search_results = {};
  $scope.toastMessage = '';


  $scope.overlaps = 0;
  let overlap_lucene = {};
  let overlap_page_rank = {};

  $scope.lucene_start = 0;
  $scope.lucene_rows = 10;
  $scope.lucene_search_term = undefined;
  $scope.lucene_corrected_search_term = undefined;
  $scope.total_lucene_results = 0;

  $scope.page_rank_start = 0;
  $scope.page_rank_rows = 10;
  $scope.page_rank_search_term = undefined;
  $scope.page_rank_corrected_search_term = undefined;
  $scope.total_page_rank_results = 0;

  $scope.undefined = undefined;

  $scope.lucene_algo_desc = 'LUCENE BASED';
  $scope.page_rank_algo_desc = 'PAGE RANK BASED';

  $('.toast').toast({delay: 4000});

  $scope.urls_table = [];
  $scope.display_type = {};
  $scope.display_type.value = true;

  $scope.suggestions = [];
  $scope.activeSuggestionIndex = -1;

  let colors = ['#0000FF', '#00FF00', '#FFFF00', '#FF00FF',
                '#00FFFF', '#800000', '#FF0000', '#008080',
                '#808080', '#000080'];

  let assignStyle = function(sourceObj, targetObj, url, id, resultsObj, model, color) {
    if(targetObj[url]) {
      if(sourceObj[url]) {
        sourceObj[url].ids.push(id);
        resultsObj[id].style = {"background-color": sourceObj[url].color};
      }
      else {
        sourceObj[url] = {};
        sourceObj[url].ids = [];
        sourceObj[url].ids.push(id);
        sourceObj[url].color = targetObj[url].color;
        resultsObj[id].style = {"background-color": sourceObj[url].color};
        for(let j = 0; j < targetObj[url].ids.length; j++)
          model[targetObj[url].ids[j]].style = {"background-color": sourceObj[url].color};
        $scope.overlaps++;
      }
    }
    else {
      if(sourceObj[url]) {
        sourceObj[url].ids.push(id);
      }
      else {
        sourceObj[url] = {};
        sourceObj[url].ids = [];
        sourceObj[url].ids.push(id);
        sourceObj[url].color = color;
      }
    }
  };

  $scope.clear = function() {

    $scope.overlaps = 0;
    overlap_lucene = {};
    overlap_page_rank = {};

    $scope.lucene_search_results = {};
    $scope.page_rank_search_results = {};

    $scope.lucene_start = 0;
    $scope.lucene_rows = 10;
    $scope.lucene_search_term = undefined;
    $scope.lucene_corrected_search_term = undefined;
    $scope.total_lucene_results = 0;

    $scope.page_rank_start = 0;
    $scope.page_rank_rows = 10;
    $scope.page_rank_search_term = undefined;
    $scope.page_rank_corrected_search_term = undefined;
    $scope.total_page_rank_results = 0;
    $scope.toastMessage = '';
    $scope.urls_table = [];
    $scope.suggestions = [];
    $scope.activeSuggestionIndex = -1;
  };

  $scope.activateSuggestionIndex = function(index) {
    $scope.activeSuggestionIndex = index;
  };

  $scope.onEnterPressed = function() {
    $scope.clear();
    $scope.search(true);
    $scope.search(false);
  };

  $scope.onKeyPress = function($event) {
    let keyCode = $event.which;
    if (keyCode === 13) {
      if($scope.activeSuggestionIndex > -1)
        $scope.search_query = $scope.suggestions[$scope.activeSuggestionIndex].term.split('_').join(' ');
      $scope.onEnterPressed();
    }
    else if (keyCode === 38) {
      if($scope.suggestions.length > 0) {
        if($scope.activeSuggestionIndex <= 0)
          $scope.activeSuggestionIndex = $scope.suggestions.length;
        $scope.activeSuggestionIndex--;
        $scope.activeSuggestionIndex %= $scope.suggestions.length;
      }
    }
    else if(keyCode === 40) {
      if($scope.suggestions.length > 0) {
        $scope.activeSuggestionIndex++;
        $scope.activeSuggestionIndex %= $scope.suggestions.length;
      }
    }
    else {
      let params = {};
      let words = $scope.search_query.split(' ');
      let last_word = words[words.length - 1];
      params.query = last_word;
      $http({
        url: '/solr/suggest',
        method: 'GET',
        params: params
      }).then(function success(response) {
        try {
          let suggestions = response.data.suggest.suggest[params.query];
          if(suggestions.hasOwnProperty('suggestions')) {
            var suggestion_list = suggestions.suggestions;
            for(let i = 0; i < suggestions.suggestions.length; i++) {
              let prev_terms = words.slice(0, words.length - 1).join(' ');
              prev_terms = prev_terms ? prev_terms + ' ' : '';
              suggestions.suggestions[i].term = prev_terms + suggestions.suggestions[i].term;
            }
            $scope.suggestions = suggestions.suggestions;
            $scope.activeSuggestionIndex = -1;
          }
        }
        catch(e) {
          console.error(e);
          $scope.toastMessage = e.name + ': ' + e.message;
          $('.toast').toast('show');
        }
      }, function error(response) {
        $scope.toastMessage = response.data.error.msg;
        $('.toast').toast('show');
      });
    }
  };
  let clearUrlsTable = function(arr, key) {
    for(let i = 0; i < arr.length; i++) {
      delete arr[i][key];
      if(!arr[i].lucene && !arr[i].page_rank) {
        arr.splice(i, 1);
        i--;
      }
    }
  };
  $scope.search = function(lucene_based) {
    $scope.overlaps = 0;
    let params = {};
    params.query = $scope.search_query;
    params.lucene_based = lucene_based;
    if(lucene_based) {
      $scope.lucene_search_term = $scope.search_query;
      params.start = $scope.lucene_start;
      params.rows = $scope.lucene_rows;
      overlap_lucene = {};
      $scope.lucene_search_results = {};
    }
    else {
      $scope.page_rank_search_term = $scope.search_query;
      params.start = $scope.page_rank_start;
      params.rows = $scope.page_rank_rows;
      overlap_page_rank = {};
      $scope.page_rank_search_results = {};
    }

    if($scope.search_query) {
      $http({
        url: '/solr/search',
        method: 'GET',
        params: params
      }).then(function success(response) {
        try {
          let results = response.data.response.docs;
          let resultsObj = {};
          if(lucene_based) {
            clearUrlsTable($scope.urls_table, 'lucene');
          }
          else {
            clearUrlsTable($scope.urls_table, 'page_rank');
          }
          for(let i = 0; i < results.length; i++) {
            resultsObj[results[i].id] = {};
            let og_url = angular.isArray(results[i].og_url) ? results[i].og_url[0] : results[i].og_url;
            if(!og_url)
              results[i].og_url = initializationData[results[i].id.split('/').pop()];
            resultsObj[results[i].id]['data'] = results[i];

            if(lucene_based) {
              if(!$scope.urls_table[i])
                $scope.urls_table[i] = {};
              $scope.urls_table[i].lucene = og_url;
              assignStyle(overlap_lucene, overlap_page_rank, results[i].og_url, results[i].id, resultsObj, $scope.page_rank_search_results, colors[i % colors.length]);
            }
            else {
              if(!$scope.urls_table[i])
                $scope.urls_table[i] = {};
              $scope.urls_table[i].page_rank = og_url;
              assignStyle(overlap_page_rank, overlap_lucene, results[i].og_url, results[i].id, resultsObj, $scope.lucene_search_results, colors[i % colors.length]);
            }
          }
          if(lucene_based) {
            $scope.lucene_search_results = resultsObj;
            $scope.total_lucene_results = response.data.response.numFound;
            $scope.lucene_start = response.data.response.start;
            $scope.lucene_rows = response.data.response.docs.length;
            $scope.lucene_corrected_search_term = response.data.response.corrected_spell;
          }
          else {
            $scope.page_rank_search_results = resultsObj;
            $scope.total_page_rank_results = response.data.response.numFound;
            $scope.page_rank_start = response.data.response.start;
            $scope.page_rank_rows = response.data.response.docs.length;
            $scope.page_rank_corrected_search_term = response.data.response.corrected_spell;
          }
        } catch(e) {
          console.error(e);
          $scope.toastMessage = e.name + ': ' + e.message;
          $('.toast').toast('show');
        }
      }, function error(response) {
        $scope.toastMessage = response.data.error.msg;
        $('.toast').toast('show');
      });
    }
  };
});

searchApp.directive('resultsTable', function() {
  return {
    restrict: 'E',
    scope: {
      search_results: '=results',
      overlaps: '=overlaps',
      algo_desc: '@algoDesc'
    },
    templateUrl: 'results-table.html',
    controller: ['$scope', function ResultsTableController($scope) {
      $scope.isArray = angular.isArray;
      $scope.isObjectEmpty = function(obj) {
        return Object.keys(obj).length === 0;
      };
      $scope.showDetails = function(result) {
        result.details = !result.details;
      };
    }]
  };
});

searchApp.directive('urlsTable', function() {
  return {
    restrict: 'E',
    scope: {
      urls_table: '=urlsTable',
    },
    templateUrl: 'urls-table.html',
    controller: ['$scope', function UrlsTableController($scope) {

    }]
  };
});

let loadPreAppInitializationData = function() {
  const initInjector = angular.injector(["ng"]);
  const $http = initInjector.get("$http");

  return $http.get("/data").then(
      function(response) {
        searchApp.constant("initializationData", response.data);
      },
      function(errorResponse) {
        searchApp.constant("initializationData", {});
      }
  );
};

loadPreAppInitializationData().then(function() {
  angular.element(document).ready(function() {
    angular.bootstrap(document, ["searchApp"]);
  });
});