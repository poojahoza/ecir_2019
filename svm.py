import pandas as pd
import numpy as np
from sklearn.svm import SVC
from sklearn.model_selection import train_test_split

train = pd.read_csv("/home/rachel/grad_courses/data_science/train_data.csv") # Check if pandas converts data to numerical values by default.
test = pd.read_csv("/home/rachel/grad_courses/data_science/test_data.csv")

X_train = train.drop('Class', axis=1)
y_train = train['Class']

svclassifier = SVC(kernel='linear')
svclassifier.fit(X_train, y_train)
y_pred = svclassifier.predict(test)
for prediction in y_pred:
    print(prediction)

