import scipy
import numpy
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import Sequential, layers, callbacks
from tensorflow.keras.layers import Dense, LSTM, Dropout, GRU, Bidirectional
import pandas as pd
import numpy as np

from  datetime import datetime

from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split

from matplotlib import pyplot as plt
from IPython.core.pylabtools import figsize

from pandas.plotting import register_matplotlib_converters
register_matplotlib_converters()
import seaborn as sns
from sqlalchemy import create_engine

#------------------------------------------------------------
# Get weather data from sensors database to use for the
# training of the GRU prediction algorithms to use for the 
# greenhouse ambient control mobile application
#-----------------------------------------------------------


#------------------------------------------------------------------------------------------------
# GRU takes a 3D input (num_samples, num_timesteps, num_features).
# This function takes create_dataset, to reshape input.
# The model makes predictions based on the last nth day data (In the first iteration of the
# for-loop, the input carries the first n days and the output is the Temperature on the nth day).
#------------------------------------------------------------------------------------------------
def create_dataset (X, y, n = 1):
    Xs, ys = [], []

    for i in range(len(X)- n):
        v = X[i:i + n, :]
        Xs.append(v)
        ys.append(y[i + n])
 
    return np.array(Xs), np.array(ys)

#-----------------------------------------
# Replace missing values by interpolation
#-----------------------------------------
def replace_missing (attribute):
    return attribute.interpolate(inplace=True)

#----------------------------------------
# Create GRU model
#----------------------------------------
def create_model(units, m):
    model = Sequential()
    # First layer of GRU
    model.add(m (units = units, return_sequences = True, input_shape = [X_train.shape[1], X_train.shape[2]]))
    #To make the LSTM and GRU networks robust to changes, the Dropout function is used.
    #Dropout(0.2) randomly drops 20% of units from the network.
    model.add(Dropout(0.1))
    model.add(m (units, return_sequences=False))
    # Second layer of GRU
    model.add(Dropout(0.1))
    model.add(Dense(50))
    model.add(Dense(1))
    #Compile model
    model.compile(loss='mse', optimizer='adam')
    return model

#---------------------------------------
# Fit GRU
#---------------------------------------
def fit_model(model):
    early_stop = keras.callbacks.EarlyStopping(monitor = 'val_loss', patience = 10)
    # shuffle = False because the order of the data matters
    history = model.fit(X_train, y_train, epochs = 100, validation_split = 0.2, batch_size = 32, shuffle = False, callbacks = [early_stop])
    return history

#---------------------------------------
# Run prediction
#---------------------------------------
def prediction(model):
    prediction = model.predict(X_test)
    prediction = scaler_y.inverse_transform(prediction)
    return prediction

#---------------------------------------
# save predicted values to the database
#---------------------------------------
def save_prediction(prediction):

    print("Writing the predicted weather data from the sensors mariadb database")

    p = pd.DataFrame(prediction)
    p['hour'] = pd.DataFrame(range(1, len(prediction) + 1))
    p.columns = ['Temperature', 'hour']
    engine = create_engine("mariadb+mariadbconnector://appuser:apppassword01@192.168.1.244:3306/sensors")
    p.to_sql(name='PREDICTION', con=engine, if_exists = 'replace', index=False)

#---------------------------------------
# Evaluate Prediction algoritm accuracy
#---------------------------------------
def evaluate_prediction(predictions, actual, model_name):
    errors = predictions - actual
    mse = np.square(errors).mean()
    rmse = np.sqrt(mse)
    mae = np.abs(errors).mean()

    print(model_name + ':')
    print('Mean Absolute Error: {:.4f}'.format(mae))
    print('Root Mean Square Error: {:.4f}'.format(rmse))
    print('')


#-------------------------------------------------------------
# Start of main function
#-------------------------------------------------------------

def main():
# Set random seed for reproducibility

    global X_train, y_train, X_test, y_test
    global scaler_x, scaler_y, input_scaler, output_scaler

    tf.random.set_seed(1234)
    print("Random seed set.")

    print("Reading the weather data from the sensors mariadb database")
    engine = create_engine("mariadb+mariadbconnector://appuser:apppassword01@192.168.1.244:3306/sensors")
    sql = "SELECT Time,Temperature,humidity,windspeed,winddir,sealevelpressure FROM WEATHER_STATION"
    df = pd.read_sql(sql, engine)
    df['Time'] = pd.to_datetime(df.Time)
    df.set_index('Time', inplace=True)


    print(df.describe())
    print(df.dtypes)

    # iterating the columns
    for col in df.columns:
        print(col)

    # Explore the first five rows
    print("Explore the first 5 rows")
    print(df.head())

    # Data description
    print("Show Data Description")
    print(df.describe())

    #print("-----------------Plots-----------------")
    #timeseries(df.index, df['Temperature'], 'Time (day)', 'Temp trend ($C^o$)')

    print("Replace missing values")
    # Check missing values
    print(df.isnull().sum())

    replace_missing(df['Temperature'])
    replace_missing(df['humidity'])
    replace_missing(df['windspeed'])
    replace_missing(df['winddir'])
    replace_missing(df['sealevelpressure'])

    # Outlier detection
    up_b = df['Temperature'].mean() + 2*df['Temperature'].std()
    low_b = df['Temperature'].mean() - 2*df['Temperature'].std()

    # Replace outlier by interpolation for base consumption
    df.loc[df['Temperature'] > up_b, 'Temperature'] = np.nan
    df.loc[df['Temperature'] < low_b, 'Temperature'] = np.nan
    df['Temperature'].interpolate(inplace=True)

    train_size = int(len(df)*0.8)
    train_dataset, test_dataset = df.iloc[:train_size], df.iloc[train_size:]

    print('Dimension of train data: ',train_dataset.shape)
    print('Dimension of test data: ', test_dataset.shape)

    # Split train data to X and y
    print("Split train data to X and y")
    X_train = train_dataset.drop('Temperature', axis = 1)
    y_train = train_dataset.loc[:,['Temperature']]

    X_test = test_dataset.drop('Temperature', axis = 1)
    y_test = test_dataset.loc[:,['Temperature']]


    # Transform X_train, y_train, X_test and y_test
    print("Transform X_train, y_train, X_test and y_test")

    # Different scaler for input and output
    print("Different scaler for input and output")
    scaler_x = MinMaxScaler(feature_range = (0,1))
    scaler_y = MinMaxScaler(feature_range = (0,1))

    # Fit the scaler using available training data
    print("Fit the scaler using available training data")
    input_scaler = scaler_x.fit(X_train)
    output_scaler = scaler_y.fit(y_train)

    # Apply the scaler to training data
    print("Apply the scaler to training data")
    train_y_norm = output_scaler.transform(y_train)
    train_x_norm = input_scaler.transform(X_train)

    # Apply the scaler to test data
    print("Apply the scaler to test data")
    test_y_norm = output_scaler.transform(y_test)
    test_x_norm = input_scaler.transform(X_test)
    print("Scalar transformation on test dataset")
    print("X_test: ", X_test)
    print("test_x_norm: ", test_x_norm)

    #prediction on the last 20 days data
    TIME_STEPS = 120

    # Create a 3D Input Dataset
    X_test, y_test = create_dataset(test_x_norm, test_y_norm, TIME_STEPS)
    X_train, y_train = create_dataset(train_x_norm, train_y_norm, TIME_STEPS)
    print('X_train.shape: ', X_test.shape)
    print('y_train.shape: ', y_train.shape)
    print('X_test.shape: ', X_test.shape) 
    print('y_test.shape: ', y_train.shape)

    #GRU has 2 hidden layers including
    #64 neurons and 1 neuron in the output layer

    # GRU and LSTM 
    model_gru = create_model(64, GRU)

    # Fit GRU
    history_gru = fit_model(model_gru)

    # Inverse target variable for train and test data
    # After building the model, the target needs to be transformed
    # variable back to original data space for train and test data using scaler_y.inverse_transform.
    # scaler_y needs to be used
    y_test = scaler_y.inverse_transform(y_test)
    y_train = scaler_y.inverse_transform(y_train)

    # Make prediction using GRU
    prediction_gru = prediction(model_gru)
    # Save the prediction data to database
    save_prediction(prediction_gru)

    # Calculate RMSE and MAE
    #evaluate_prediction(prediction_gru, y_test, 'GRU')

if __name__ == "__main__":
    main()
