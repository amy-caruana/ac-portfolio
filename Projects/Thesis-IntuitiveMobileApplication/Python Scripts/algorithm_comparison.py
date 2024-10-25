import scipy
import numpy
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import Sequential, layers, callbacks
from tensorflow.keras.layers import Dense, LSTM, Dropout, GRU, Bidirectional
import pandas as pd
import numpy as np

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
# training of the LSTM and GRU prediction algorithms
# in order to determine the best algorihm to use for the 
# greenhouse ambient control mobile application
#-----------------------------------------------------------

#-------------------------------------------
# Define a function to draw time_series plot
#-------------------------------------------
def timeseries (x_axis, y_axis, x_label, y_label):
    plt.figure(figsize = (10, 6))
    plt.plot(x_axis, y_axis, color ='black')
    plt.xlabel(x_label, {'fontsize': 12})
    plt.ylabel(y_label, {'fontsize': 12})
    plt.show()
    #plt.savefig('C:/Temp/Trend1.jpg', format='jpg', dpi=1000)

#-------------------------------------------
# Define a function to draw a histogram of
# temperature values distribution
#-------------------------------------------
def plot_histogram(x):
    plt.hist(x, bins = 19, alpha=0.8, color = 'gray', edgecolor = 'black')
    plt.title("Histogram of '{var_name}'".format(var_name=x.name))
    plt.xlabel("Value")
    plt.ylabel("Frequency")
    plt.show()

#------------------------------------------------------------------------------------------------
# LSTM and GRU take a 3D input (num_samples, num_timesteps, num_features).
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
# Create LSTM or GRU model
#----------------------------------------
def create_model(units, m):
    model = Sequential()
    # First layer of LSTM
    model.add(m (units = units, return_sequences = True, input_shape = [X_train.shape[1], X_train.shape[2]]))
    #To make the LSTM and GRU networks robust to changes, the Dropout function is used.
    #Dropout(0.2) randomly drops 20% of units from the network.
    model.add(Dropout(0.1))
    model.add(m (units, return_sequences=False))
    # Second layer of LSTM             
    model.add(Dropout(0.1))
    model.add(Dense(50))
    model.add(Dense(1))
    #Compile model
    model.compile(loss='mse', optimizer='adam')
    return model

#---------------------------------------
# Fit LSTM and GRU
#---------------------------------------
def fit_model(model):
    early_stop = keras.callbacks.EarlyStopping(monitor = 'val_loss', patience = 10)
    # shuffle = False because the order of the data matters
    history = model.fit(X_train, y_train, epochs = 100, validation_split = 0.2, batch_size = 32, shuffle = False, callbacks = [early_stop])
    return history

#---------------------------------------
# Plot loss value
#---------------------------------------
def plot_loss (history, model_name):
    plt.figure(figsize = (10, 6))
    plt.plot(history.history['loss'])
    plt.plot(history.history['val_loss'])
    plt.title('Model Train vs Validation Loss for ' + model_name)
    plt.ylabel('Loss')
    plt.xlabel('epoch')
    plt.legend(['Train loss', 'Validation loss'], loc='upper right')
    #plt.savefig('C:/Temp/loss_'+model_name+'.jpg', format='jpg', dpi=1000)
    plt.show()

#---------------------------------------
# Run prediction
#---------------------------------------
def prediction(model):
    prediction = model.predict(X_test)
    prediction = scaler_y.inverse_transform(prediction)
    return prediction

#---------------------------------------
# Plot predicted future values
#---------------------------------------
def plot_future(prediction, model_name, y_test):
    
    plt.figure(figsize=(10, 6))
    
    range_future = len(prediction)

    plt.plot(np.arange(range_future), np.array(y_test), label='True Future')
    plt.plot(np.arange(range_future), np.array(prediction),label='Prediction')

    plt.title('True future vs prediction for ' + model_name)
    plt.legend(loc='upper left')
    plt.xlabel('Time (Hourly)')
    plt.ylabel('Temperature ($C^o)')
    #plt.savefig('C:/Temp/predic_'+model_name+'.jpg', format='jpg', dpi=1000)
    plt.show()

#---------------------------------------
# Plot predicted future values after
# actual historical data (to check
# to check continuity
#---------------------------------------
def plot_trend(prediction, model_name, y_train):
    
    plt.figure(figsize=(10, 6))
    
    range_future = len(prediction)
    range_historical = len(y_train)

    plt.plot(np.arange(range_historical), np.array(y_train), label='True Past')
    plt.plot(np.arange(range_historical, range_historical + range_future), np.array(prediction),label='Future Prediction')

    plt.title('Historical and prediction for ' + model_name)
    plt.legend(loc='upper left')
    plt.xlabel('Time (Hourly)')
    plt.ylabel('Temperature ($C^o)')
    #plt.savefig('C:/Temp/predic_'+model_name+'.jpg', format='jpg', dpi=1000)
    plt.show()

#---------------------------------------
# save predicted values to the database
#---------------------------------------
def save_prediction(prediction):
    
    print("Writing the predicted weather data from the sensors mariadb database")
    
    p = pd.DataFrame(prediction)
    p['hour'] = pd.DataFrame(range(1, len(prediction) + 1))
    p.columns = ['Temperature', 'hour']
    engine = create_engine("mariadb+mariadbconnector://appuser:apppassword01@192.168.1.244:3306/sensors")
    p.to_sql(name='PREDICTION', con=engine, if_exists = 'append', index=False)

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

    print("-----------------Plots-----------------")
    timeseries(df.index, df['Temperature'], 'Time (day)', 'Temp trend ($C^o$)')
    plot_histogram(df['Temperature'])

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

    # Plot train and test data
    plt.figure(figsize = (10, 6))
    plt.plot(train_dataset.Temperature)
    plt.plot(test_dataset.Temperature)
    plt.xlabel('DateTime')
    plt.ylabel('Temperature (Temp trend ($C^o$)')
    plt.legend(['Train set', 'Test set'], loc='upper right')
    #plt.savefig('C:/Temp/2.jpg', format='jpg', dpi=1000)
    plt.show()

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
    TIME_STEPS = 30

    # Create a 3D Input Dataset
    X_test, y_test = create_dataset(test_x_norm, test_y_norm, TIME_STEPS)
    X_train, y_train = create_dataset(train_x_norm, train_y_norm, TIME_STEPS)
    print('X_train.shape: ', X_test.shape)
    print('y_train.shape: ', y_train.shape)
    print('X_test.shape: ', X_test.shape) 
    print('y_test.shape: ', y_train.shape)

    #LSTM and GRU have 2 hidden layers including
    #64 neurons and 1 neuron in the output layer

    # GRU and LSTM 
    model_gru = create_model(64, GRU)
    model_lstm = create_model(64, LSTM)

    # Fit LSTM and GRU
    history_lstm = fit_model(model_lstm)
    history_gru = fit_model(model_gru)


    # Plot train loss vs validation loss
    plot_loss (history_lstm, 'LSTM')
    plot_loss (history_gru, 'GRU')

    # Inverse target variable for train and test data
    # After building the model, the target needs to be transformed
    # variable back to original data space for train and test data using scaler_y.inverse_transform.
    # scaler_y needs to be used
    y_test = scaler_y.inverse_transform(y_test)
    y_train = scaler_y.inverse_transform(y_train)

    # Make prediction using LSTM and GRU
    prediction_lstm = prediction(model_lstm)
    prediction_gru = prediction(model_gru)

    plot_future(prediction_lstm, 'LSTM', y_test)
    plot_future(prediction_gru, 'GRU', y_test)

    plot_trend(prediction_gru, 'GRU', y_train)
    save_prediction(prediction_gru)

    # Calculate RMSE and MAE
    evaluate_prediction(prediction_lstm, y_test, 'LSTM')
    evaluate_prediction(prediction_gru, y_test, 'GRU')

if __name__ == "__main__":
    main()
