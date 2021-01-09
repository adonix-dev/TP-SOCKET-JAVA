package Server;

import as.ListeAuthAdapt;

public class UDPMgtServer extends UDPServer{

    public UDPMgtServer(int port) {
        super("UDP Management", null, port);
    }


    @Override
    protected String worker(String messageIn){
        try{
            String[] params = messageIn.split(" ");

            if(params[0].equals("") || params[1].equals("") || params[2].equals("")){
                throw new Exception("Mauvaise entree");
            }

            switch (params[0]){
                case "CHK":
                    this.displayServerMessage("CHECKING " + params[1] + " " + params[2]);
                    if (ListeAuthAdapt.db.tester(params[1], params[2])) {
                        this.displayServerMessage("GOOD " + params[1] + " " + params[2]);
                        return "GOOD";
                    } else {
                        this.displayServerErrorMessage("BAD " + params[1] + " " + params[2]);
                        return "BAD";
                    }
                case "DEL":
                    if (ListeAuthAdapt.db.supprimer(params[1], params[2])) {
                        this.displayServerMessage("DONE " + params[1] + " " + params[2]);
                        return "DONE";
                    } else {
                        this.displayServerErrorMessage("ERROR " + params[1] + " " + params[2]);
                        return "ERROR";
                    }
                case "MOD":
                    if (ListeAuthAdapt.db.mettreAJour(params[1], params[2])) {
                        this.displayServerMessage("DONE " + params[1] + " " + params[2]);
                        return "DONE";
                    } else {
                        this.displayServerErrorMessage("ERROR " + params[1] + " " + params[2]);
                        return "ERROR";
                    }
                case "ADD":
                    if (ListeAuthAdapt.db.creer(params[1], params[2])) {
                        this.displayServerMessage("DONE " + params[1] + " " + params[2]);
                        return "DONE";
                    } else {
                        this.displayServerErrorMessage("ERROR " + params[1] + " " + params[2]);
                        return "ERROR";
                    }
                default:
                    this.displayServerErrorMessage("Requete recu invalide.");
                    this.displayServerErrorMessage("usage : CHK|DEL|MOD|ADD user password");
                    return "ERROR bad_request";
            }

        }
        catch (Exception e){
            this.displayServerErrorMessage("Requete recu invalide.");
            this.displayServerErrorMessage("usage : CHK|DEL|MOD|ADD user password");
            //this.displayServerErrorMessage(e.getMessage());
            return "ERROR bad_request";
        }
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    protected void start() {
        super.initListener();
        while(true) {
            super.listen();
            String answer = this.worker(super.clientData);
            super.answer(answer);
        }
    }

    public static void main(String[] args) {

        UDPMgtServer MgtAS = new UDPMgtServer(12345);
        MgtAS.start();
        MgtAS.stop(); //never accessed
    }
}
