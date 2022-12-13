# android_project_earthquake_notification

Νικόλαος Διονύσιος Ζαπάντης icsd17047

https://github.com/NikosZap/android_project_earthquake_notification

Σχετικά με το ανέβασμα του project απο το android studio στο github, δυσκολεύτηκα πάρα πολύ να το κάνω upload στο repository της σχολής και για αυτο το ανέβασα σε δικό μου repository. Μετά απο πολλές προσπάθειες παρακολουθόντας tutorial και διαβάζοντας οδηγίες, με εγκατάσταση του git, στην προσπάθεια να το κάνω push στο url του repository της σχολής, μου πετούσε σφάλματα τόσο στην αυθεντικοποίηση, όσο και στο token ενώ προσπάθουσα να διαβεβαιωθώ αν κάνω κάτι λάθος. Στο repository της σχολής ανεβάζω το project σε μορφή zip και στο προσωπικό repository το
project μέσω του git.

Στην εφαρμογή, ο χρήστης κατα την είσοδο συναντά μία φόρμα απο προεραίτικές επιλογές που του επιτρέπει να ρυθμίσει τις παραμέτρους. Όλες οι παραμέτροι που δεν τροποποιούνται έχουν default τιμες για την ομαλή εκτέλεση του κώδικα. Στο κουμπι "current location" αποθηκεύονται οι συντεταγμένες της τοποθεσίας του χρήστη χωρίς να ανοίγει οχάρτης. Στο κουμπί "open google maps" ο χρήστης μπορεί να αναζητήσει την περιοχή που τον ενδιαφέρει και η τελευταία πινέζα του χάρτη αποθηκεύεται σαν θέση επιλογής.
![319527742_538638401484270_4874887613289246302_n](https://user-images.githubusercontent.com/83024306/207444139-f2e3451d-4112-4f5e-993b-60d13e52f8e9.jpg)

![318745576_710772383778050_1325324118196723601_n](https://user-images.githubusercontent.com/83024306/207444199-f686d0aa-a6ce-4c60-9842-2b83faecbf89.jpg)

Με το κουμπι "apply" ξεκινάει η διαδικασία για την ανάγνωση νέων σεισμών απο το web αρχειο.

![319269591_1583649948773400_6301884069764262883_n](https://user-images.githubusercontent.com/83024306/207444434-b852a677-a408-42a2-893b-540cfc48dfb3.jpg)

Η διαδικασία αυτή δεν θα την έλεγα αποδοτική και σίγουρα υπάρχουν καλύτεροι και πιο αποδοτικοι αλγόριθμοι, ωστόσω ο πρωτότυπος αλγόριθμος που έγραψα αρχικά στο netbeans δούλευε ικανοποιητικά αλλά η μεταφορά του στο android studio προκάλεσε κάποια ζητήματα με την ανάγνωση των σεισμών που δεν μπόρεσα να εντοπίσω και να διορθώσω. Όταν εντοπίζεται σεισμός στο χρονικό πλαίσιο που θέτουμε, δημιουργείται notification με τα στοιχεία χαρακτηριστικά του σεισμού.
Αν ο χρήστης πατήσει το notification, ανοίγει χάρτης με πινέζα στο σημείο του σεισμού και με όλες τις πλροφορίες.

![318620827_688281939356134_2100127602574102818_n](https://user-images.githubusercontent.com/83024306/207444676-0684ba59-b78b-4bd9-9572-14cd796347eb.jpg)

![317912423_1691374101315291_906712188302587767_n](https://user-images.githubusercontent.com/83024306/207445179-6a549038-3be9-4f9f-bb12-18cfa28887d6.jpg)




Μια τέτοια εφαρμογή πρεπει να μην είναι ολη την ώρα ανοικτή, πρεπει να δουλεύει στο παρασκήνιο. Λόγω χρόνου, δέν κατάφερα να βρώ τρόπο να λειτουργεί έτσι η εφαρμογή,
ωστόσο απο αναζήτηση που έκανα αυτό επιτυγχάνεται με extend στην BroadcastingReceiver.  
