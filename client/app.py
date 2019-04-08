from flask import Flask, render_template, request, redirect, jsonify, make_response
from test_data import Rooms
from test_history import JackColeHistory, JohnHoneyHistory
from datetime import datetime
import requests
import json

app = Flask(__name__)

Rooms = Rooms()
JC = JackColeHistory()
JH = JohnHoneyHistory()


@app.route('/')
def current():
    return render_template('current.html', rooms=Rooms)


@app.route('/upload', methods=['post'])
def upload():

    r = requests.post('http://localhost:8080/changetemp', data=json.dumps({
        "upper": 99.3, "lower": 16.7, "currentTemp": 21.0,
        "dateTime": "now", "lab": 1,
        "flags": {"sensor1Flag": True, "sensor2Flag": True, "sensor3Flag": True}
    }),
        headers={'Content-type': 'application/json'
                 })
    print(r.status_code)
    print(r.text)
    return render_template('current.html', rooms=Rooms)


@app.route('/history')
def history():
    print("hello")
    r = requests.get('http://localhost:8080/history')
    print(r.text)
    return render_template('history.html', rooms=Rooms, jc=JC, jh=JH)


if __name__ == '__main__':
    app.run(debug=True)
