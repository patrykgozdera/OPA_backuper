clear all;
close all;

w= linspace
X(w)=1/(1+j.*w);

figure, plot(w, abs(X)), title('Amplitude plot');
figure, plot(w, angle(X)), title('Phase plot');