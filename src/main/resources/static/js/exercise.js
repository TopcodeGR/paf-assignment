angular.module('exercise', [])
    .run(function($rootScope, $http) {

        $rootScope.getTournaments = function() {
            $http.get('/exercise', {
                params: {
                    operation: 'getTournaments'
                }
            }).then(function(result) {
                $rootScope.tournaments = result.data;
                angular.forEach($rootScope.tournaments, function(tournament) {
                    $rootScope.getPlayersInTournament(tournament.tournament_id);
                });
            });
        };

        $rootScope.addTournament = function(rewardAmount) {
            $http.get('/exercise', {
                params: {
                    operation: 'addTournament',
                    reward_amount: rewardAmount
                }
            }).then(function(result) {
                $rootScope.rewardAmount = undefined;
                $rootScope.getTournaments();
            });
        };

        $rootScope.editTournament = function(tournament) {
            $rootScope.editTournamentId = tournament.tournament_id;
            $rootScope.editRewardAmount = tournament.reward_amount;
        };

        $rootScope.updateTournament = function(tournamentId, rewardAmount) {
            $http.get('/exercise', {
                params: {
                    operation: 'updateTournament',
                    tournament_id: tournamentId,
                    reward_amount: rewardAmount
                }
            }).then(function(result) {
                $rootScope.editTournamentId = undefined;
                $rootScope.editRewardAmount = undefined;
                $rootScope.getTournaments();
            });
        };

        $rootScope.removeTournament = function(tournamentId) {
            $http.get('/exercise', {
                params: {
                    operation: 'removeTournament',
                    tournament_id: tournamentId
                }
            }).then(function(result) {
                $rootScope.getTournaments();
            });
        };

        $rootScope.addPlayerIntoTournament = function(tournamentId, playerName) {
            $http.get('/exercise', {
                params: {
                    operation: 'addPlayerIntoTournament',
                    tournament_id: tournamentId,
                    player_name: playerName
                }
            }).then(function(result) {
                $rootScope.playerTournamentId = undefined;
                $rootScope.playerName = undefined;
                $rootScope.getPlayersInTournament(tournamentId);
            });
        };

        $rootScope.removePlayerFromTournament = function(tournamentId, playerId) {
            $http.get('/exercise', {
                params: {
                    operation: 'removePlayerFromTournament',
                    tournament_id: tournamentId,
                    player_id: playerId
                }
            }).then(function(result) {
                $rootScope.getPlayersInTournament(tournamentId);
            });
        };

        $rootScope.getPlayersInTournament = function(tournamentId) {
            $http.get('/exercise', {
                params: {
                    operation: 'getPlayersInTournament',
                    tournament_id: tournamentId
                }
            }).then(function(result) {
                var index = $rootScope.tournaments.findIndex(function(element) {
                    return element.tournament_id == tournamentId;
                });
                $rootScope.tournaments[index].players = result.data;
            });
        };

        $rootScope.getTournaments();
    });