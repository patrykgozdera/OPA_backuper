clear all
close all
clc

%OSTRZE¯ENIE: bezmyœlne u¿ywanie skryptu ma z³y wp³yw na twoj¹ wiedzê
%i potencjalnie z³y wp³yw na wyniki z laborki. U¿ywaæ ze zrozumieniem.
%Wszelkie b³êdy w skrypcie nie wynikaj¹ z chêci wyeliminowania konkurencji
%na rynku pracy.

%jednostki to: kOhm, mA, V, Hz

%Dane do projektu
Rg = 3.9;
Ro = 22;
fd = 1200;

%Dane z laborki WE-1
Icq = 1.19;
Rc = 4.7;
Re = 1.8;
R1 = 18;
R2 = 5.6;
Uce = 4.26;

%zak³adana tolerancja rezystorów
tolerancja = 0.05;

%jedziemy

RL = (Ro*Rc)./(Ro+Rc);

ku0 = -(Icq/0.025)*RL;
Rdz = (R1*R2)/(R1+R2);
reb = 0.025 / Icq;
rbemin = 200*reb;
rbemax = 400*reb;
Rwemin = (Rdz*rbemin)/(Rdz+rbemin);
Rwemax = (Rdz*rbemax)/(Rdz+rbemax);
kus0min = (Rwemin/(Rwemin + Rg))*ku0;
kus0max = (Rwemax/(Rwemax + Rg))*ku0;

%badamy rozrzut wszystkiego
Rc_1 = Rc - tolerancja*Rc;
Rc_2 = Rc + tolerancja*Rc;
Re_1 = Re - tolerancja*Re;
Re_2 = Re + tolerancja*Re;
R1_1 = R1 - tolerancja*R1;
R1_2 = R1 + tolerancja*R1;
R2_1 = R2 - tolerancja*R2;
R2_2 = R2 + tolerancja*R2;
Rg_1 = Rg - tolerancja*Rg;
Rg_2 = Rg + tolerancja*Rg;
Ro_1 = Ro - tolerancja*Ro;
Ro_2 = Ro + tolerancja*Ro;

%powtarzamy obliczenia

RL_1 = (Ro_1*Rc_1)./(Ro_1+Rc_1);
ku0_1 = -(Icq/0.025)*RL_1;
Rdz_1 = (R1_1*R2_1)/(R1_1+R2_1);
Rwemin_1 = (Rdz_1*rbemin)/(Rdz_1+rbemin);
Rwemax_1 = (Rdz_1*rbemax)/(Rdz_1+rbemax);
kus0min_1 = Rwemin_1/(Rwemin_1 + Rg_1)*ku0_1;
kus0max_1 = Rwemax_1/(Rwemax_1 + Rg_1)*ku0_1;

RL_2 = (Ro_2*Rc_2)./(Ro_2+Rc_2);
ku0_2 = -(Icq/0.025)*RL_2;
Rdz_2 = (R1_2*R2_2)/(R1_2+R2_2);
Rwemin_2 = (Rdz_2*rbemin)/(Rdz_2+rbemin);
Rwemax_2 = (Rdz_2*rbemax)/(Rdz_2+rbemax);
kus0min_2 = Rwemin_2/(Rwemin_2 + Rg_2)*ku0_2;
kus0max_2 = Rwemax_2/(Rwemax_2 + Rg_2)*ku0_2;

max_odchylka_ku0 = (ku0_2 - ku0_1)/ku0;
max_odchylka_kus0 = (kus0max_2 - kus0min_1)/mean([kus0min, kus0max]);

%dynamika
Uwymax_dolna = Uce - 0.2;
Uwymax_gorna = Icq*RL;

dynamika = min(Uwymax_dolna, Uwymax_gorna);

%czas na obliczenie czêstotliwoœci dolnej granicznej
Rtemp1 = (Rg*Rdz)/(Rg + Rdz);
Rtemp2 = Rtemp1/400;
Rce = Rtemp2 + reb;
Rce = (Rce*Re)/(Re + Rce);

C3 = 1e3/(2*pi*Rce*fd); % zeby wyszlo w uf mnozymy razy 1000

disp(['Wzmocnienie zwykle wynosi ', num2str(ku0), ' V/V, rozrzut rezystorów powoduje jego niepewnoœæ na poziomie ', num2str(max_odchylka_ku0*100), '%.']);
disp(['Wzmocnienie skuteczne wynosi od ', num2str(kus0min), ' do ', num2str(kus0max), ' V/V, niepewnoœæ tych wartoœci, liczona od ich œredniej, wynosi ', num2str(max_odchylka_kus0*100), '%.']);
disp(['Max. amplituda na wyjœciu wynosi ', num2str(dynamika), ' V.']);

if(Uwymax_dolna > Uwymax_gorna)
    disp('Powodem obcinania jest zatkanie.');
else
    disp('Powodem obcinania jest nasycenie.');
end

disp(['Najmniejsza mo¿liwa rezystancja widziana przez Ce wynosi ', num2str(Rce*1e3), ' ohm. ']);
disp(['Kondensator Ce musi byæ wiêkszy od ', num2str(C3), ' uF.']);