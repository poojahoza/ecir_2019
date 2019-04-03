import pandas as pd
import numpy as np
from sklearn.svm import SVC
from sklearn.model_selection import train_test_split

data = pd.read_csv("/home/rachel/grad_courses/data_science/datacsv.csv", error_bad_lines=False) # Check if pandas converts data to numerical values by default.
data.dropna()
print(data.dtypes == np.complex_)
X = data.drop('Class', axis=1)
y = data['Class']
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.20)
svclassifier = SVC(kernel='linear')
svclassifier.fit(X_train, y_train)
'''y_pred = svclassifier.predict(X_test)
print(confusion_matrix(y_test,y_pred))
print(classification_report(y_test,y_pred))'''