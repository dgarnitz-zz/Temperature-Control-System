from flask import Flask, render_template, request, redirect, jsonify, make_response
from test_data import Rooms
from test_history import JackColeHistory, JohnHoneyHistory
from datetime import datetime

app = Flask(__name__)

Rooms = Rooms()
JC = JackColeHistory()
JH = JohnHoneyHistory()


@app.route('/')
def current():
    return render_template('current.html', rooms=Rooms)


@app.route('/history')
def history():
    return render_template('history.html', rooms=Rooms, jc=JC, jh=JH)


if __name__ == '__main__':
    app.run(debug=True)
