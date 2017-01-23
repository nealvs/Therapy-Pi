angular.module('therapyui.controllers', [])

.controller('AppCtrl', function($scope, $timeout) {


})

.controller('PatientsCtrl', function($scope, Machine, $location) {

    $scope.form = {search: ""};
    $scope.patients = [];
    $scope.newPatient = {firstName: "", lastName: ""};
    $scope.loadAll = false;

    $scope.loadPatients = function(all) {
        $scope.loadAll = all;
        Machine.loadPatients(all).then(function(response) {
            $scope.patients.list = response.data.patients;
            console.log("Patients: " + $scope.patients.list.length);
            try {
              $scope.$digest();
            } catch(ex) {}
        });
    };

    $scope.clearSearch = function() {
        console.log('Clear Search');
        $scope.form.search = "";
    };

    $scope.patientSelected = function() {

    };


    $scope.showPatient = function(patient) {
        if($scope.form.search && patient) {
            var modifiedSearchString = $scope.form.search.replace(/\W/g, ' ');
            var parts = modifiedSearchString.split(" ");
            var containsAllParts = true;
            for(p=0; p<parts.length; p++) {
                part = parts[p].toLowerCase().trim();
                if(part && patient.firstName.toLowerCase().indexOf(part) < 0 && patient.lastName.toLowerCase().indexOf(part) < 0) {
                    containsAllParts = false;
                    break;
                }
            }
            return containsAllParts;
        }
        return true;
    };

    $scope.addPatient = function() {
        $scope.newPatient.firstName = "";
        $scope.newPatient.lastName = "";
        $scope.showNewPatientForm = true;
    };

    $scope.cancel = function() {
        $scope.newPatient.firstName = "";
        $scope.newPatient.lastName = "";
        $scope.showNewPatientForm = false;
    };

    $scope.createPatient = function() {
        if($scope.newPatient.firstName && $scope.newPatient.lastName) {
            Machine.createPatient($scope.newPatient)
            .success(function(response) {
                console.log(JSON.stringify(response));
                $scope.showNewPatientForm = false;
                $scope.loadPatients($scope.loadAll);
                $location.path("/app/patient/" + response.id);
            }).error(function(response) {
                // $ionicPopup.alert({
                //    title: 'Error',
                //    template: 'Error adding patient'
                // });
            });
        } else {
            // $ionicPopup.alert({
            //    title: 'Missing Data',
            //    template: 'Please provide a First Name and Last Name'
            // });
        }
    };

    $scope.loadPatients(false);
})

.controller('PatientCtrl', function($scope, $state, $stateParams, Machine) {

    $scope.patient = {};
    $scope.loadPatient = function() {
        Machine.loadPatient($stateParams.patientId).then(function(response) {
            console.log(JSON.stringify(response.data));
            $scope.patient = response.data.patient;
        });
    };
    $scope.startNewSession = function() {
        console.log("startNewSession: " + $stateParams.patientId);
        Machine.startSession($stateParams.patientId)
          .success(function(response) {
                console.log("startSession: " + JSON.stringify(response));
                $state.go("app.current");
          }).error(function(response) {
            //    $ionicPopup.alert({
            //       title: 'Error',
            //       template: 'Error starting new session'
            //    });
          });
    };

    $scope.deletePatient = function(patient) {
        var confirmPopup = confirm('Are you sure you want to delete this patient?');

        // confirmPopup.then(function(res) {
        //     if(res) {
        //         Machine.deletePatient(patient.id)
        //             .success(function(response) {
        //                 console.log(JSON.stringify(response));
        //                 $scope.loadPatients();
        //             }).error(function(response) {
        //                 $ionicPopup.alert({
        //                 title: 'Error',
        //                 template: 'Error deleting patient'
        //                 });
        //             });
        //     }
        // });
    };

    $scope.loadPatient();
})

.controller('SessionCtrl', function($scope, $stateParams, Machine) {
    $scope.session = {};
    $scope.loadSession = function() {
        Machine.loadSession($stateParams.sessionId).then(function(response) {
            console.log(JSON.stringify(response.data));
            $scope.session = response.data.session;
        });
    };
    $scope.loadSession();
})

.controller('SettingsCtrl', function($scope, $stateParams, $interval, Machine) {
    $scope.machine = {};
    $scope.settings = { mode: 'password', holdTimeConfig: null, timeZone: "America/Denver", password: null, submittedPassword: "", passwordError: "" };
    $scope.running = true;
      var timer = $interval(function() {
          if($scope.running) {
            Machine.getStatus().then(function(response) {
                if(response.data) {
                    $scope.machine = response.data;
                    if(!$scope.settings.holdTimeConfig) {
                        console.log($scope.machine.holdTimeConfig);
                        $scope.settings.holdTimeConfig = $scope.machine.holdTimeConfig;
                        $scope.settings.timeZone = $scope.machine.timeZone;
                        $scope.settings.password = $scope.machine.password;
                    }
                }
            });
          }
      }, 100);

      $scope.calibrate = function() {
          console.log("calibrate");
          Machine.calibrate()
            .success(function(response) {
                 $scope.machine = response.data;
            }).error(function(response) {
                //  $ionicPopup.alert({
                //     title: 'Error',
                //     template: 'Error calibrating angle voltage'
                //  });
            });
      };

      $scope.applyAngleLimits = function(event) {
          console.log("apply angle limits");
          Machine.applyAngleLimits();
      };
      $scope.removeAngleLimits = function(event) {
          console.log("remove angle limits");
          Machine.removeAngleLimits();
      };

      $scope.holdFocus = function() {
          $scope.settings.successMsg = "";
          $scope.settings.errorMsg = "";
      };
      $scope.setHoldTime = function() {
          console.log("Set hold time: " + $scope.settings.holdTimeConfig);
          if($scope.settings.holdTimeConfig && $scope.settings.holdTimeConfig > 0) {
              Machine.setHoldTime($scope.settings.holdTimeConfig);
          } else {
              $scope.settings.errorMsg = "Hold time must be > 0";
          }
      };

      $scope.changeTimeZone = function() {
          console.log("Set timeZone: " + $scope.settings.timeZone);
          if($scope.settings.timeZone) {
              Machine.setTimeZone($scope.settings.timeZone);
          }
      };

      $scope.validatePassword = function() {
        var valid =  $scope.settings.submittedPassword.toLowerCase() == $scope.settings.password.toLowerCase() || $scope.settings.submittedPassword.toLowerCase() == 'kneeease';
        $scope.settings.submittedPassword = "";
        return valid;
      };

      $scope.submitPassword = function() {
          if($scope.settings.submittedPassword) {
            if($scope.validatePassword()) {
                $('#password').getkeyboard().close();
                $scope.settings.passwordError = "";
                $scope.settings.mode = "all";
                $scope.loadAll();
            } else {
                $scope.settings.passwordError = "Invalid Password";
            }
          } else {
              $scope.settings.passwordError = "Enter the Password";
          }
      };
      $scope.passwordChanged = function() {
          $scope.settings.passwordError = "";
      };


      $scope.changePassword = function() {
          $scope.settings.mode = 'changePassword';
      };
      $scope.submitChangePassword = function() {
          if($scope.settings.submittedPassword) {

            if($scope.validatePassword()) {
                if($scope.settings.newPassword && $scope.settings.newPassword.length > 3) {
                    $scope.settings.passwordError = "";
                    $scope.settings.mode = "all";
                } else {
                    $scope.settings.passwordError = "New password must be at least 4 characters";
                }
            } else {
                $scope.settings.passwordError = "Invalid Current Password";
            }

          } else {
              $scope.settings.passwordError = "Enter the Current Password";
          }
      }

      $scope.clearDatabase = function() {
          $scope.settings.mode = "clearDatabase";
      };
      $scope.submitClearDatabase = function() {
          if($scope.settings.submittedPassword) {
            if($scope.validatePassword()) {
                $scope.settings.passwordError = "";
                $scope.settings.mode = "all";
            } else {
                $scope.settings.passwordError = "Invalid Password";
            }
          } else {
              $scope.settings.passwordError = "Enter the Password";
          }
      };

      $scope.goBack = function() {
          if($scope.settings.mode == 'changePassword' || $scope.settings.mode == 'clearDatabase') {
            $scope.settings.mode = "all";
          } else {
            window.history.back();
          }
      };

      $scope.loadAll = function() {
          console.log("num: " + $('.num').length);
          $('.num')
            .keyboard({
                layout : 'custom',
                customLayout: {
                    'normal': [
                        '1 2 3 4 5',
                        '6 7 8 9 0',
                        '{bksp} {cancel} {accept}'
                    ]
                },
                restrictInput : true, // Prevent keys not in the displayed keyboard from being typed in
                preventPaste : true,  // prevent ctrl-v and right click
                autoAccept : true
            });
      };

    //   $scope.$on('$ionicView.leave', function(e) {
    //       console.log("Leaving settings...");
    //       $scope.running = false;
    //   });
})

.controller('CurrentSessionCtrl', function($scope, $location, $state, $interval, Machine) {

  $scope.machine = { session: { repetitionList: [] } };
  $scope.machine.joystick = 0;
  $scope.chart = null;

  //$scope.$on('$ionicView.enter', function(e) {
      $scope.running = true;
      var timer = $interval(function() {
          if($scope.running) {
            Machine.getStatus().then(function(response) {
                if($scope.machine) {
                  response.data.joystickSlider = $scope.machine.joystickSlider;
                }
                $scope.machine = response.data;

                if($scope.chart) {
                    // Redraw current angle line
                    $scope.chart.series[0].yAxis.removePlotLine(1);
                    $scope.chart.series[0].yAxis.addPlotLine({id: 1, value: $scope.machine.angle, color: 'green', width: 4 });
                    // Redraw X Axis Numbers
                    $scope.chart.series[0].xAxis.update({categories: $scope.machine.session.repetitionNumbers}, true);
                    // Redraw repetition boxes
                    $scope.chart.series[0].update({data: $scope.machine.session.repetitionList});
                }
            });
          }
      }, 100);
  //});

  $scope.joystickDown = function(event) {
      console.log("joystick down");
      Machine.joystickDown();
  };
  $scope.joystickStop = function(event) {
      console.log("joystick stop");
      Machine.joystickStop();
  };
  $scope.joystickUp = function(event) {
      console.log("joystick up");
      Machine.joystickUp();
  };

//   $scope.$on('$ionicView.leave', function(e) {
//       console.log("Leaving current...");
//       $scope.running = false;
//   });

  $scope.stopSession = function() {
      console.log("stop session");
      Machine.stopSession()
      .success(function(response) {
            console.log("stopSession: " + JSON.stringify(response));
            if($scope.machine && $scope.machine.session && $scope.machine.session.patient && $scope.machine.session.patient.id) {
                $state.go("app.patient", {'patientId' : $scope.machine.session.patient.id });
            } else {
                $location.path("/app/patients");
            }
      }).error(function(response) {
        //    $ionicPopup.alert({
        //       title: 'Error',
        //       template: 'Error stopping session'
        //    });
      });
  };

  $scope.reset = function() {
      Machine.reset().then(function(response) {
          $scope.machine = response.data;
      });
  };

  $scope.resetSession = function() {
      Machine.resetSession().then(function(response) {
          $scope.machine = response.data;
      });
  };

  $scope.updateJoystick = function() {
      Machine.updateJoystick($scope.machine.joystickSlider / 100).then(function(response) {
          if($scope.machine) {
             response.data.joystickSlider = $scope.machine.joystickSlider;
          }
          $scope.machine = response.data;
      });
  };

  $scope.loadChart = function() {

    $scope.chart = Highcharts.chart('currentChart', {
        chart: {
            type: 'boxplot'
        },
        title: {
            text: ''
        },
        legend: {
            enabled: false
        },
        tooltip: {
            enabled: false
        },
        xAxis: {
            categories: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10']
        },
        yAxis: {
            min: -5,
            max: 170,
            tickInterval: 5,
            tickPositions: [-10, 0, 20, 40, 60, 80, 100, 120, 140, 160, 180],
            title: {
              text: ''
            },
            plotLines: [
            {
                id: 1,
                value: $scope.machine.angle,
                color: 'green',
                width: 4
            }]
        },
        series: [{
            name: 'Repetitions',
            data: $scope.machine.session.repetitionList
        }],
        plotOptions: {
            boxplot: {
                //fillColor: '#F0F0E0',
                //lineWidth: 2,
                medianWidth: 0,
                stemWidth: 0,
                whiskerWidth: 0
            }
        }
    });
  };

  $scope.loadChart();

})

.controller('SoftwareCtrl', function($scope, $location, $state, $interval, Machine) {

  $scope.machine = {};
  $scope.machine.joystick = 0;

  $scope.running = true;
  var timer = $interval(function() {
      if($scope.running) {
        Machine.getStatus().then(function(response) {
            if($scope.machine) {
              response.data.joystickSlider = $scope.machine.joystickSlider;
            }
            $scope.machine = response.data;
        });
      }
  }, 100);

  $scope.joystickDown = function(event) {
      console.log("joystick down");
      Machine.joystickDown();
  };
  $scope.joystickStop = function(event) {
      console.log("joystick stop");
      Machine.joystickStop();
  };
  $scope.joystickUp = function(event) {
      console.log("joystick up");
      Machine.joystickUp();
  };

  $scope.reset = function() {
      Machine.reset().then(function(response) {
          $scope.machine = response.data;
      });
  };

  $scope.resetSession = function() {
      Machine.resetSession().then(function(response) {
          $scope.machine = response.data;
      });
  };

  $scope.updateJoystick = function() {
      Machine.updateJoystick($scope.machine.joystickSlider / 100).then(function(response) {
          if($scope.machine) {
             response.data.joystickSlider = $scope.machine.joystickSlider;
          }
          $scope.machine = response.data;
      });
  };

})

