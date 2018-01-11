import os
import matplotlib.pyplot as plt
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.neural_network import MLPRegressor

# read csv
data = pd.read_csv(r"D:\workspace\results.csv")

# define consts
split = 0.7
x_columns = list(["size"])
diffs = {"duration": 2500, "usedMemoryMax": 350000, "systemCpuLoadMax": 0.006,
         "systemCpuLoadAvg": 0.0006}

# split data to train and test parts
data_train, data_test = train_test_split(data, test_size=0.3)

# try to find out best model for one of output
for y_column in ["duration", "usedMemoryMax", "systemCpuLoadMax"]:
    # define data
    max_diff = diffs[y_column]
    X = data_train[x_columns].values
    y = data_train[y_column].values
    X_test = data_test[x_columns].values
    y_test = data_test[y_column].values

    # print about experiment
    print("---------------------------------------------------")
    print("|Try to build regression for {0} -> {1}. \n"
          "|Max possible prediction error {2}\n"
          "|Average y: {3}".format(x_columns, y_column, max_diff, sum(y) / len(y)))
    print("---------------------------------------------------")

    # Try to find best model
    for solver in ["lbfgs", "sgd", "adam"]:
        for activation in ["identity", "logistic", "tanh", "relu"]:
            mlp = MLPRegressor(solver=solver, hidden_layer_sizes=(2, 3, 2),
                               max_iter=1500, shuffle=True, random_state=1,
                               activation=activation)
            mlp.fit(X, y)
            right_count = 0

            try:
                for i in range(0, X_test.size):
                    result = mlp.predict([X_test[i]])[0]
                    if abs(result - y_test[i]) < max_diff:
                        right_count += 1
            finally:
                print("|Activation function: {0:8s} | Learn algorithm {1:6s} | Percent: {2:5f}|".format(activation,
                                                                                                        solver,
                                                                                                        right_count * 1.0 / y_test.size))
    print("---------------------------------------------------")

# mlpDuration = MLPRegressor(solver="sgd", hidden_layer_sizes=(2, 3, 2),
#                                max_iter=1500, shuffle=True, random_state=1,
#                                activation="tanh")
#
# mlpDuration.fit(data_train[x_columns], data_train["systemCpuLoadMax"])
#
# plt.plot(mlpDuration.predict(data[x_columns].values), 'r--', data["systemCpuLoadMax"].values, 'g--' )
# plt.ylabel('CP usage prediction')
# plt.show()