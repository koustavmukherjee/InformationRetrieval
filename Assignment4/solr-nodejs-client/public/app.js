'use strict';

// Declare app level module which depends on views, and core components
angular.module('myApp', [
]).
config(['$locationProvider', function($locationProvider) {
  $locationProvider.hashPrefix('!');
}]).
controller('main',function($scope, $http){
  $scope.search_query = undefined;
  $scope.lucene_search_results = [];
  $scope.page_rank_search_results = [];
  $scope.search = function(lucene_based) {
    if($scope.search_query) {
      $http({
        url: '/solr/search',
        method: 'GET',
        params: {query: $scope.search_query}
      }).then(function success(response) {
        try {
          $scope.lucene_search_results = response.data.response.docs;
        } catch(e) {
          console.error(e);
        }
      }, function error(response) {
        console.error(response);
      });
    }
  };
  $scope.showDetails = function(result) {
    result.details = !result.details;
  };
});