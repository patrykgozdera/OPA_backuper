clear all;
close all
clc
h=[0.4,0.08,0.04,0.008,0.004];

x0=0;
% t1=0:0.01:4;
% xdok=2*(1 - exp(-t1/0.5));
% hold off;
% plot(t1,xdok,'k');
% hold on;
for k=1:length(h)
    t=0:h(k):4;
    x(1)=x0;
    xt(1)=x0;
    N=length(t);
    for i=1:N-1
        xdok(i+1)=2*(1 - exp(-t(i)/0.5));
        x(i+1) = x(i) + h(k)*(4-2*x(i));
        xt(i+1)=(xt(i) + 4*h(k) - h(k)*xt(i))./(1+h(k));
        xz(i+1)=xz(i) + h(k)*
    end
    error(k) = max(abs(x-xdok));
    errort(k) = max(abs(xt-xdok));
    figure(k+1)
    plot(t,x,'r',t,xt,'b',t,xdok,'k');
end

figure(1)
loglog(h,error,'.r',h,errort,'.b');

