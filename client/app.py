from flask import Flask, render_template, request, redirect, flash, url_for, logging, session
# from test_data import Rooms
# from test_history import JackColeHistory, JohnHoneyHistory
from datetime import datetime
import requests
import json

app = Flask(__name__)

# Rooms = Rooms()
# JC = JackColeHistory()
# JH = JohnHoneyHistory()

user = "username"
password = "password"
rooms = []
currentUpdate = {}


@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password_candidate = request.form['password']
        credentials = {'useranme': username, 'password': password_candidate}
        # response = requests.post(
        #  'http://localhost:8080/login', data = credentials)

        # res = "true"
        # if response.text == 'true':
        if username == user and password == password_candidate:
            flash('Login Successful', 'success')
            redirect(url_for('base'))
        else:
            error = "Invalid username or password"
            return render_template('login.html', error=error)

    return render_template('login.html')


@app.route('/')
def current():
    global currentUpdate
    r = requests.get('http://localhost:8080/history/current')
    object = json.loads(r.text)
    currentUpdate = object
    return render_template('current.html', rooms=[currentUpdate])


@app.route('/upload', methods=['post'])
def upload():

    # print(currentUpdate)
    lower = request.form["lower"]
    upper = request.form["upper"]
    currentUpdate["lower"] = lower
    currentUpdate["upper"] = upper
    # print(currentUpdate)

    r = requests.post('http://localhost:8080/update/changetemp', data=json.dumps(
        currentUpdate), headers={'Content-type': 'application/json'})
    '''
        {
        "upper": 99.3, "lower": 16.7, "currentTemp": 21.0,
        "dateTime": "now", "lab": 1,
        "flags": {"sensor1Flag": True, "sensor2Flag": True, "sensor3Flag": True}
        }
    '''

    if (r.status_code != 200):
        flash("Failed to connect to server.  Try again.", 'danger')
        return render_template('current.html')
    else:
        newUpdate = json.loads(r.text)
        return render_template('current.html', rooms=[newUpdate])


@app.route('/history')
def history():
    global rooms
    r = requests.get('http://localhost:8080/history/rooms')

    print(r.text)
    rooms = json.loads(r.text)

    return render_template('history.html', rooms=rooms)


@app.route("/viewhistory", methods=["post"])
def view_history():
    room_id = request.form["option"]
    print(room_id)
    r = requests.get('http://localhost:8080/history/view{}'.format(room_id))

    print(r.text)
    room = json.loads(r.text)

    return render_template('history.html', rooms=rooms, lab=room)


@app.route('/logout')
def logout():
    session.clear()
    flash("Logged Out", 'success')
    return redirect(url_for('login'))


if __name__ == '__main__':
    app.secret_key = "secret"
    app.run(debug=True)
