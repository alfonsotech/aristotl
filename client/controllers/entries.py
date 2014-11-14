from flask import render_template, Blueprint, request

from client.models import SEP

blueprint = Blueprint('entries', __name__)

@blueprint.route("/sep/<slug>")
def sep(slug):
    c = SEP(slug).content
    return render_template("article.html", article=c)
