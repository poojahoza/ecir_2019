import pandas as pd
import numpy as np
from sklearn.svm import SVC
from sklearn import metrics
from sklearn.model_selection import train_test_split

'''train = pd.read_csv("/home/rachel/grad_courses/data_science/train_data.csv") # Check if pandas converts data to numerical values by default.
test = pd.read_csv("/home/rachel/grad_courses/data_science/test_data.csv")

X_train = train.drop('Class', axis=1)
y_train = train['Class']

svclassifier = SVC(kernel='rbf')
svclassifier.fit(X_train, y_train)
y_pred = svclassifier.predict(test)
for prediction in y_pred:
    print(prediction)'''

####### Evaluate the resutls using the combined train and test set #######
data = pd.read_csv("/home/rachel/grad_courses/data_science/train_data.csv")
features = data.drop('Class', axis=1)
targets = data['Class']

x_train, x_test, y_train, y_test = train_test_split(data, targets, test_size=0.3, random_state=109)
svclassifier = SVC(kernel='rbf')
svclassifier.fit(x_train, y_train)
y_pred = svclassifier.predict(x_test)

print("####### Rbf Kernal #######")
print("Accuracy: ", metrics.accuracy_score(y_test, y_pred))
print("Precision: ", metrics.precision_score(y_test, y_pred))
print("Recall: ", metrics.recall_score(y_test, y_pred))