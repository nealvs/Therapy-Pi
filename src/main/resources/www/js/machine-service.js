angular.module('therapyui.machine-service', [])

.factory('Machine', function(Common) {
    var service = {};

    service.getStatus = function() {
        return Common.get('/status');
    };

    service.loadPatients = function(all) {
        return Common.get('/patientList?all=' + all);
    };
    service.loadPatient = function(id) {
        return Common.get('/patient/' + id);
    };

    service.createPatient = function(patient) {
        return Common.post('/createPatient', patient);
    };
    service.deletePatient = function(patientId) {
        return Common.post('/deletePatient', {'id': patientId});
    };

    service.startSession = function(patientId) {
        return Common.post('/startSession', {'patientId': patientId});
    };
    service.resetSession = function() {
        return Common.post('/resetSession', {});
    };
    service.stopSession = function() {
        return Common.post('/stopSession', {});
    };
    service.loadSession = function(id) {
        return Common.get('/session/' + id);
    };

    service.calibrate = function() {
        return Common.post('/settings/calibrate');
    };
    service.setHoldTime = function(holdTime) {
        return Common.post('/settings/setHoldTime', {'value': holdTime});
    };
    service.setTimeZone = function(timeZone) {
        return Common.post('/settings/setTimeZone', {'value': timeZone});
    };

    service.updateJoystick = function(value) {
        return Common.post('/updateJoystick', {'value': value});
    };

    service.applyAngleLimits = function() {
        return Common.post('/applyAngleLimits', {});
    };
    service.removeAngleLimits = function() {
        return Common.post('/removeAngleLimits', {});
    };

    service.joystickUp = function() {
        return Common.post('/joystickUp', {});
    };
    service.joystickStop = function() {
        return Common.post('/joystickStop', {});
    };
    service.joystickDown = function() {
        return Common.post('/joystickDown', {});
    };

    service.reset = function(value) {
        return Common.post('/reset', {});
    };

    return service;
});
