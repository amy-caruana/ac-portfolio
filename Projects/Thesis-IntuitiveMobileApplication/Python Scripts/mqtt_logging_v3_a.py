import os
import json
import paho.mqtt.client as mqtt
import mariadb
import sys
from time import time


MQTT_HOST = ''
MQTT_CLIENT_ID = ''
MQTT_USER = ''
MQTT_PASSWORD = ''
TOPIC = ''

DATABASE_FILE = ''

def on_connect(mqtt_client, user_data, flags, conn_result):
	mqtt_client.subscribe(TOPIC)

def on_message(mqtt_client, user_data, message):
	sensor_model=0
	payload = message.payload.decode('utf-8')
	print(f"{message.topic} {payload} {int(time())}")
	msg=json.loads(payload)
	sensid = message.topic.split('/')
	print("Sensor_id: ", sensid[1])

	sql = "SELECT sensor_model FROM SENSORS where sensor_id=\'" + sensid[1] + "\'"
	try:
		conn = mariadb.connect(
		user="",
		password="",
		host="",
		database="")
	except mariadb.Error as e:
		print(f"Error connecting to MariaDB Platform: {e}")
		sys.exit(1)
	print("Connected to the database sucessfully")
	# Get Cursor
	cur = conn.cursor()

	try:
		cur.execute(sql)
		# get all records
		row = cur.fetchone()
		print("Total number of rows in table: ", cur.rowcount)
		print(row)
		if row[0] == 'LHT65':
			sensor_model = 1
			print("Sensor type is LHT65")
		elif row[0] == 'LSE01':
			sensor_model = 2
			print("Sensor type is LSE01")

	except mariadb.Error as e:
		print(f"Error: {e}")
	finally:
		if conn:
			conn.close()
			cur.close()
			print("MySQL connection is closed")

	if sensor_model == 1:
		print("Humidity SHT: ", msg["Hum_SHT"])
		print("Battery level: ", msg["BatV"])
		print("Temperature C DS: ", msg["TempC_DS"])
		print("RSSI: ", msg["RSSI"])
		print("Temp C SHT: ", msg["TempC_SHT"])
		print("SNR: ", msg["SNR"])
		sql = "INSERT INTO LHT65 (sensor_id,Hum_SHT,BatV,TempC_DS,RSSI,TempC_SHT,SNR,EXT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
		val = (sensid[1], msg["Hum_SHT"], msg["BatV"], msg["TempC_DS"], msg["RSSI"], msg["TempC_SHT"], msg["SNR"], msg["EXT"])
	elif sensor_model == 2:
		print("Battery level: ", msg["BatV"])
		print("Soil Temperature (C): ", msg["TempC_Soil"])
		print("RSSI: ", msg["RSSI"])
		print("Soil Mositure (%): ", msg["Moist"])
		print("Soil EC (uS): ", msg["Cond"])
		print("SNR: ", msg["SNR"])
		sql = "INSERT INTO LSE01 (sensor_id,BatV,TempC_SOIL,Moisture_SOIL,EC_SOIL,RSSI,SNR) VALUES (?, ?, ?, ?, ?, ?, ?)"
		val = (sensid[1], msg["BatV"], msg["TempC_Soil"], msg["Moist"], msg["Cond"], msg["RSSI"], msg["SNR"])
	else:
		return

	# Connect to MariaDB Platform
	try:
		conn = mariadb.connect(
		user="",
		password="",
		host="",
		database="")
	except mariadb.Error as e:
		print(f"Error connecting to MariaDB Platform: {e}")
		sys.exit(1)
	print("Connected to the database sucessfully")
	# Get Cursor
	cur = conn.cursor()

	try:
		cur.execute(sql, val)
		conn.commit()
		cur.close()
		conn.close()
		print("MariaDB connection closed")
	except mariadb.Error as e:
		print(f"Error: {e}")

	#to mirror data in a remote database

        # Connect to MariaDB Platform
	try:
		conn = mariadb.connect(
		user="",
		password="",
		host="",
		database="")
	except mariadb.Error as e:
		print(f"Error connecting to MariaDB Platform: {e}")
		sys.exit(1)
	print("Connected to the remote database sucessfully")
	# Get Cursor
	cur = conn.cursor()

	try:
		cur.execute(sql, val)
		conn.commit()
		cur.close()
		conn.close()
		print("MariaDB connection closed")
	except mariadb.Error as e:
		print(f"Error: {e}")

def main():

	mqtt_client = mqtt.Client(MQTT_CLIENT_ID)
	mqtt_client.username_pw_set(MQTT_USER, MQTT_PASSWORD)

	mqtt_client.on_connect = on_connect
	mqtt_client.on_message = on_message

	mqtt_client.connect(MQTT_HOST, MQTT_PORT)
	mqtt_client.loop_forever()

main()
