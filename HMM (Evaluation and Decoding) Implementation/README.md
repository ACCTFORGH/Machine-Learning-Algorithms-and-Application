<h1>Hidden Markov Model (Evaluation and Decoding)</h1>

<p>
The program is to tag the part-of-speech (PoS) for text data. The evaluation and decoding stage of HMM is implemented here with pre-trained HMM parameters.<br><br>
Files Explanined: <br><br>
hmm-trans.txt, hmm-emit.txt and hmm-prior.txt: These files contain pre-trained model parameters of an HMM that will be used in Evaluation and Decoding problems. hmm-trans.txt contain the probabilities of transferring from one state to another. hmm-emit.txt is the probability of each word seen in a certain state (PoS). hmm-prior.txt is the initial prior probability for each state.<br><br>

train.txt and dev.txt are the training and development text data files. Sentences in these files have been pre-processed and tokenized.<br><br>

alpha.java: forward algorithm implementation<br>
beta.java: backward algorithm implementation. alpha.java and beta.java should output the same values for the same input. <br>
viterbi.java: viterbi algorithm (de-coding) algorithm implementation<br>

<br>
Commands for running the programs after compiling:<br>
1. java alpha dev.txt hmm-trans.txt hmm-emit.txt hmm-prior.txt <br>
2. java beta dev.txt hmm-trans.txt hmm-emit.txt hmm-prior.txt <br>
3. java viterbi dev.txt hmm-trans.txt hmm-emit.txt hmm-prior.txt <br>


</p>
