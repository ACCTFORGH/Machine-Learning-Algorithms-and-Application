<h2>Naive Bayes Classifiers And the Modified Versions</h2>

<p>
The Naive Bayes Classifiers are to classify a political blog as being “liberal” or “conservative”. After implementing the basic algorithm, the program will be extended in a few ways.<br><br>

Explanation of Each File:<br>
nb.java: basic Naive Bayes Classifier<br>
topwords.java: prints out the top 20 words with the highest word probabilities <br>
nbStopWords.java: Naive Bayes Classifier after excluding the top N words with highest probabilities<br>
smoothing.java: Naive Bayes Classifier with smoothing in place<br>
topwordsLogOdds.java: print out the top 20 words with the highest log-odds ratio for each class<br>


After compiling the java files, follow the the commands below for starting the program: <br><br>

1. Extract the training and testing files to the same folder as the java files<br>
2. Run the commands below for each file <br>
   java nb split.train split.test<br>
   java topwords split.train <br>
   java nbStopWords split.train split.test 10 <br>
   (here 10 is the size of the stop words to be truncated. You can change to any value to see which is the optimal parameter) <br>
   java smoothing split.train split.test 1 <br>
   (1 is the value of the smoothing parameter)<br>
   java topwordsLogOdds split.train <br>
</p>
