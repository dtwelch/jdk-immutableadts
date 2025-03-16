# algebraic lists, maps, and sets (jdk 23+)

Used some of the newer features of java to
create short and (hopefully) efficient immutable
collections. Specifically provides the following
immutable structures:

* **sorted map adt** (`VTreeMap`) - implemented using the Arne-Andersson
  tree described in [this paper](https://arxiv.org/abs/1412.4882)
  by Prabhakar Ragde; this development also incorporates some bug
  fixes to the algorithms listed in the paper
    * courtesy of [this](https://github.com/m-fleury/isabelle-emacs/blob/Isabelle2024-vsce/src/HOL/Data_Structures/AA_Set.thy)
      formally verified Isabelle development

* **list adt** `VList` - a fairly standard immutable polymorphic list type modeled algebraically using
  sealed interfaces

* **tree set adt** `VTreeSet` - a polymorphic tree set that is implemented using the balanced
  Arne-Andersson tree mentioned

**NOTE:** This isn't so much a library as it is a collection of reusable half complete
immutable data structures (that is far smaller scope and intent than vavr, e.g.). So don't
rely on it for a huge production system. Requires jdk23+

Contains some unit tests and property tests (via `jqwik`). I expect additional methods will
be added and tested in future versions. Good enough for now.