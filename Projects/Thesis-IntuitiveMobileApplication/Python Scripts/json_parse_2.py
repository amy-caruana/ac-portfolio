import json
import pandas as pd
import numpy as np
import sys
import mariadb
import requests
import datetime
#------------------------------------------------------
#
# This program calls the weather.visualcrossing.com API
# to download hourly weather data for Malta.
# The data is loaded into the sensors database to be
# used for training the neural network prediction
# algorithm.
#
#
# API call format
# https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/[location]/[date1]/[date2]?key=YOUR_API_KEY
#
#------------------------------------------------------

today = datetime.datetime.now()
yesterday = today - datetime.timedelta(days=1)
yesterday_string = yesterday.strftime('%Y-%m-%d')

url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Malta/" + yesterday_string + "/" + yesterday_string + "?key=45JK6LTZ24P5AC9V7NMBHJ5CT"

print (url)

# A GET request to the API
response = requests.get(url)

# Print the response
data = response.json()
print(data)

for i in data['days']:
	date = i['datetime']
	for l in i['hours']:
		Time = date + " " + l['datetime']
		Temperature = round(((l['temp'] - 32)/1.8),1)
		humidity = l['humidity']
		windspeed = round((l['windspeed']*1.60934),1)
		winddir = l['winddir']
		sealevelpressure = l['pressure']
		print("time: ",Time, " Temp: ",Temperature, " Hum: ",humidity," WindSpd: ",windspeed, " WindDir: ",winddir, " Pressure: ",sealevelpressure)
		sql = "INSERT INTO WEATHER_STATION (Time,Temperature,humidity,windspeed,winddir,sealevelpressure) VALUES (" + "'" + Time + "'," + str(Temperature) + "," + str(humidity) + "," + str(windspeed) + "," + str(winddir) + "," + str(sealevelpressure) + ")"
		print(sql)
		# Connect to MariaDB Platform
		try:
			conn = mariadb.connect(user="",password="",host="192.168.1.244",port=3306,database="sensors")
		except mariadb.Error as e:
			print(f"Error connecting to MariaDB Platform: {e}")
			sys.exit(1)
		print("Connected to the database sucessfully")
		# Get Cursor
		cur = conn.cursor()
		try:
			cur.execute(sql)
			conn.commit()
			cur.close()
			conn.close()
			print("MariaDB connection closed")
		except mariadb.Error as e:
			print(f"Error: {e}")
