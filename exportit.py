# testt
import json
import os
import zipfile
import collections



def zipexport(tutkimusid, essee, jsondata, num):
    # Määritellään ziptiedostolle polku "/penna/temp" kansioon
    pennapath = os.path.dirname(os.path.dirname(os.path.dirname(__file__)))
    path = os.path.join(pennapath, 'temp', str(tutkimusid) + ".zip")

    # Avataan oikea zip ja luodaan uusi tiedosto
    zip = zipfile.ZipFile(path, "a")
    tiedosto = str("daFxxxx_" + num + ".txt")

    data = json.loads(jsondata, object_pairs_hook=collections.OrderedDict)
    try:
        # Luodaan valiaikainen tiedosto kirjoittamista varten
        with open(tiedosto, "a") as file:
            file.write(os.linesep + os.linesep)
            # Esseen otsikko tallennetaan erikseen, koska se kirjoitetaan aina viimeiseksi
            esseeotsikko = ""
            # inner2 tallennetaan että sen perusteella voidaan tarkistaa täytyykö erotella vastauksia pilkulla
            tempinner2 = {}
            # Lahdetaan looppaamaan lapi json-muodossa olevia vastauksia
            for key in data:
                inner = data[key]
                for key in inner:
                    # Jos kyseessä on essee niin otetaan otsikko talteen ja ohitetaan
                    if key == "tyyppi" and inner[key] == "essee":
                        esseeotsikko = inner['otsikko']
                        break
                    # Lomakkeen osan otsikko
                    elif key == "otsikko":
                        file.write(inner[key] + ": ")
                    # kirjoitetaan tiedostoon käyttäjän vastaukset (optiot)
                    elif key == "optiot":
                        inner2 = inner[key]
                        for key in inner2:
                            if inner2[key] != "":
                                if inner2 == tempinner2:
                                    file.write(", ")
                                # Otetaan inner2 talteen seuraavaa kierrosta varten
                                tempinner2 = inner2
                                # Jos arvo on "True", kirjoitetaan avaimen sisaltama tieto tiedostoon
                                if inner2[key] == "True":
                                    file.write(key)
                        file.write(os.linesep)

            file.write(os.linesep + os.linesep)
            # kirjoitetaan tiedostoon itse essee
            file.write(esseeotsikko + os.linesep)
            file.write(os.linesep)
            file.write(essee)
            file.close()
        # kopioidaan luotu tiedosto zip-tiedostoon
        zip.write(tiedosto)
        # suljeen zip-tiedosto
        zip.close()
        # poistetaan valiaikainen tiedosto (ei siis zipin sisalta, vaan juuresta)
        os.remove(tiedosto)

        file.close()
    except IOError:
        return False
    return True


def lomakeExport(tutkimusid, jsondata):
    data = json.JSONDecoder(object_pairs_hook=collections.OrderedDict).decode(jsondata)

    pennapath = os.path.dirname(os.path.dirname(os.path.dirname(__file__)))
    path = os.path.join(pennapath, 'temp', str(tutkimusid) + ".json")
    try:
        # Tiedoston ollessa auki tallennetaan OrderedDict tiedostoon.
        with open(path, 'w') as tiedosto:
            # Käytetään json.dump kun tallennetaan sanakirjaa tiedostoon.
            json.dump(data, tiedosto, indent=4, ensure_ascii=False, separators=(',', ': '))
        # Suljetaan tiedosto lopuksi.
        tiedosto.close()
    except IOError:
        return False
    return True
